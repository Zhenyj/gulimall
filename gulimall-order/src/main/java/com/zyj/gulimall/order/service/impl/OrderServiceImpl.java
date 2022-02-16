package com.zyj.gulimall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.exception.NoStockException;
import com.zyj.common.to.SkuHasStockVo;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.common.utils.R;
import com.zyj.common.vo.FareVo;
import com.zyj.common.vo.MemberAddressVo;
import com.zyj.common.vo.MemberRespVo;
import com.zyj.common.vo.OrderItemVo;
import com.zyj.gulimall.order.constant.OrderConstant;
import com.zyj.gulimall.order.dao.OrderDao;
import com.zyj.gulimall.order.entity.OrderEntity;
import com.zyj.gulimall.order.entity.OrderItemEntity;
import com.zyj.gulimall.order.enume.OrderStatusEnum;
import com.zyj.gulimall.order.feign.CartFeignService;
import com.zyj.gulimall.order.feign.MemberFeignService;
import com.zyj.gulimall.order.feign.ProductFeignService;
import com.zyj.gulimall.order.feign.WareFeignService;
import com.zyj.gulimall.order.interceptor.LoginUserInterceptor;
import com.zyj.gulimall.order.service.OrderItemService;
import com.zyj.gulimall.order.service.OrderService;
import com.zyj.gulimall.order.to.OrderCreateTo;
import com.zyj.gulimall.order.to.WareSkuLockTo;
import com.zyj.gulimall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    OrderDao orderDao;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        /**
         * feign远程调用会创建一个新的请求,会丢失请求头，包括cookie信息等
         * 所以被调用的远程服务会获取不到用户登录的session信息
         * 因为feign执行远程调用时，会调用RequestInterceptor的apply方法,所以可以自定义一个RequestInterceptor
         */

        // 执行异步调用时，会丢失请求数据，提前获取原始请求数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 1、查询用户收获地址列表
        CompletableFuture<Void> memberAddressFuture = CompletableFuture.runAsync(() -> {
            // 在异步线程中，设置之前保存的请求数据，避免丢失请求头等问题
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r = memberFeignService.getAddressByMemberId(memberRespVo.getId());
            if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                List<MemberAddressVo> addressVos = r.getData(new TypeReference<List<MemberAddressVo>>() {
                });
                orderConfirmVo.setAddress(addressVos);
            }
        }, executor);

        // 2、远程查询购物车所有选中的购物项
        CompletableFuture<Void> itemsFuture = CompletableFuture.runAsync(() -> {
            // 在异步线程中，设置之前保存的请求数据，避免丢失请求头等问题
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r = cartFeignService.getCurrentUserCartItems();
            if (!Constant.SUCCESS_CODE.equals(r.getCode())) {
                log.error(r.getMsg());
                throw new RuntimeException(r.getMsg());
            }
            List<OrderItemVo> orderItemVos = r.getData(new TypeReference<List<OrderItemVo>>() {
            });
            orderConfirmVo.setItems(orderItemVos);
        }, executor);
        // 查询商品库存
        CompletableFuture<Void> wareFuture = itemsFuture.thenRunAsync(() -> {
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            log.info("远程调用库存服务查询库存信息");
            R r = wareFeignService.getSkuHasStock(skuIds);
            if (!Constant.SUCCESS_CODE.equals(r.getCode())) {
                log.error(r.getMsg());
                throw new RuntimeException(r.getMsg());
            }

            List<SkuHasStockVo> skuHasStockVos = r.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            if (skuHasStockVos == null || skuHasStockVos.size() != skuIds.size()) {
                log.error(BizCodeEnum.PRODUCT_WARE_EXCEPTION.getMsg());
                throw new RuntimeException(BizCodeEnum.PRODUCT_WARE_EXCEPTION.getMsg());
            }
            Map<Long, Boolean> skuHasStockVoMap = skuHasStockVos.stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
            orderConfirmVo.setStocks(skuHasStockVoMap);
        }, executor);

        // 3、查询用户积分
        Long integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        // 计算其他数据

        // 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        // TODO 如果拼接用户id会导致不能
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token,
                30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        CompletableFuture.allOf(memberAddressFuture, itemsFuture, wareFuture).get();

        return orderConfirmVo;
    }

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(OrderConstant.SubmitOrderResp.SUCCESS.getCode());
        MemberRespVo member = LoginUserInterceptor.loginUser.get();
        log.info("使用lua脚本验证令牌");
        // 1、验证令牌(对比和删除必须保持原子性)，所以使用lua脚本
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        String orderToken = vo.getOrderToken();
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + member.getId()), orderToken);
        if (result == null || result == 0L) {
            responseVo.setCode(OrderConstant.SubmitOrderResp.TIME_OUT_ERROR.getCode());
            return responseVo;
        } else {
            // 2、创建订单，订单项
            OrderCreateTo order = createOrder();
            // 3、验证价格,金额对比
            BigDecimal payAmount = order.getOrder().getPayAmount();
            if (Math.abs(payAmount.subtract(vo.getPayPrice()).doubleValue()) > 0.01) {
                responseVo.setCode(OrderConstant.SubmitOrderResp.PRICE_ERROR.getCode());
                return responseVo;
            }
            // 4、保存订单
            saveOrder(order);
            // 5、库存锁定
            List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setSkuId(item.getSkuId());
                orderItemVo.setCount(item.getSkuQuantity());
                return orderItemVo;
            }).collect(Collectors.toList());
            WareSkuLockTo lockTo = new WareSkuLockTo();
            lockTo.setOrderSn(order.getOrder().getOrderSn());
            lockTo.setLocks(orderItemVos);
            //为了保证高并发 库存服务自己回滚,自动解锁库存（延时队列）
            R r = wareFeignService.orderLockStock(lockTo);
            if (!Constant.SUCCESS_CODE.equals(r.getCode())) {
                log.warn(r.getMsg());
                throw new NoStockException(r.getMsg());
            }
            // 订单创建创建成功发送消息给mq
            log.info("orderSn:{}订单创建成功,发送消息MQ", order.getOrder().getOrderSn());
            rabbitTemplate.convertAndSend("order.event.exchange", "order.create.order", order.getOrder());
            // TODO 删除购物车已选中的商品项
            responseVo.setOrder(order.getOrder());
        }
        return responseVo;
    }

    /**
     * 保存订单
     */
    public void saveOrder(OrderCreateTo order) {
        log.info("保存订单，订单号:{}", order.getOrder().getOrderSn());
        // 订单
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        save(orderEntity);
        // 订单项
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);

    }

    /**
     * 创建订单
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1、构建订单
        OrderEntity orderEntity = buildOrder();

        // 2、获取订单商品项
        List<OrderItemEntity> orderItemEntities = buildOrderItems();

        // 3、计算价格
        computePrice(orderEntity, orderItemEntities);

        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(orderItemEntities);

        return orderCreateTo;
    }

    private void computePrice(OrderEntity order, List<OrderItemEntity> items) {
        BigDecimal total = new BigDecimal(0);
        //订单的总额：叠加每一个订单项的总额信息
        BigDecimal coupon = new BigDecimal(0);
        BigDecimal integration = new BigDecimal(0);
        BigDecimal promotion = new BigDecimal(0);
        BigDecimal gift = new BigDecimal(0);
        BigDecimal growth = new BigDecimal(0);
        for (OrderItemEntity item : items) {
            coupon = coupon.add(item.getCouponAmount());
            integration = integration.add(item.getIntegrationAmount());
            promotion = promotion.add(item.getPromotionAmount());
            total = total.add(item.getRealAmount());
            gift = gift.add(new BigDecimal(item.getGiftIntegration()));
            growth = growth.add(new BigDecimal(item.getGiftGrowth()));
        }
        //1.订单价格相关
        order.setTotalAmount(total);
        //设置应付总额
        order.setPayAmount(total.add(order.getFreightAmount()));
        order.setPromotionAmount(promotion);
        order.setIntegrationAmount(integration);
        order.setCouponAmount(coupon);
        //设置积分登信息
        order.setIntegration(gift.intValue());
        order.setGrowth(growth.intValue());
    }

    /**
     * 构建订单
     */
    private OrderEntity buildOrder() {
        OrderEntity orderEntity = new OrderEntity();

        MemberRespVo member = LoginUserInterceptor.loginUser.get();
        orderEntity.setMemberId(member.getId());

        // 1、订单号，使用雪花算法
        long orderSn = IdUtil.getSnowflake().nextId();
        orderEntity.setOrderSn(String.valueOf(orderSn));
        log.info("准备构建订单,订单号:{}", orderEntity);

        // 2、获取收获信息和运费
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        log.info("远程调用库存服务获取运费等信息");
        R r = wareFeignService.getFare(orderSubmitVo.getAddrId());
        if (!r.getCode().equals(Constant.SUCCESS_CODE)) {
            log.error(r.getMsg());
            throw new RuntimeException(r.getMsg());
        }
        FareVo fareVo = r.getData(new TypeReference<FareVo>() {
        });
        // 运费
        orderEntity.setFreightAmount(fareVo.getFare());
        // 收获人信息
        MemberAddressVo address = fareVo.getAddress();
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());

        // 其他信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     */
    private List<OrderItemEntity> buildOrderItems() {
        log.info("远程调用购物车服务，获取用户购物车中选中的商品信息");
        R cartItemsResp = cartFeignService.getCurrentUserCartItems();
        if (!cartItemsResp.getCode().equals(Constant.SUCCESS_CODE)) {
            log.error(cartItemsResp.getMsg());
            throw new RuntimeException(cartItemsResp.getMsg());
        }
        List<OrderItemVo> orderItemVos = cartItemsResp.getData(new TypeReference<List<OrderItemVo>>() {
        });
        if (orderItemVos != null && orderItemVos.size() > 0) {
            List<OrderItemEntity> orderItemEntities = orderItemVos.stream().map(orderItemVo -> {
                OrderItemEntity orderItemEntity = buildOrderItem(orderItemVo);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return orderItemEntities;
        }
        return null;
    }

    /**
     * 构建订单项
     */
    private OrderItemEntity buildOrderItem(OrderItemVo orderItemVo) {
        OrderItemEntity orderItem = new OrderItemEntity();
        //1、订单信息
        //2、商品的spu信息
        Long skuId = orderItemVo.getSkuId();
        log.info("获取skuId:{}商品的spu信息", skuId);
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        if (r.getCode().equals(Constant.SUCCESS_CODE)) {
            SpuInfoVo spuInfo = r.getData(new TypeReference<SpuInfoVo>() {
            });
            orderItem.setSpuId(spuInfo.getId());
            orderItem.setSpuBrand(spuInfo.getBrandId().toString());
            orderItem.setSpuName(spuInfo.getSpuName());
            orderItem.setCategoryId(spuInfo.getCatalogId());
        }
        //3、商品的sku信息
        orderItem.setSkuId(orderItemVo.getSkuId());
        orderItem.setSkuName(orderItemVo.getTitle());
        orderItem.setSkuPrice(orderItemVo.getPrice());
        orderItem.setSkuPic(orderItemVo.getImage());
        String skuAttr = StringUtils.join(orderItemVo.getSkuAttr(), ",");
        orderItem.setSkuAttrsVals(skuAttr);
        orderItem.setSkuQuantity(orderItemVo.getCount());
        //4、优惠信息（不做）
        //5、积分信息
        orderItem.setGiftGrowth(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());
        orderItem.setGiftIntegration(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());
        //6、订单项的价格信息
        orderItem.setPromotionAmount(new BigDecimal(0));
        orderItem.setCouponAmount(new BigDecimal(0));
        orderItem.setIntegrationAmount(new BigDecimal(0));
        //订单项实际金额
        BigDecimal origin = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity()));
        ///总额减去各个优惠
        BigDecimal subtract = origin.subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getPromotionAmount())
                .subtract(orderItem.getIntegrationAmount());
        orderItem.setRealAmount(subtract);
        return orderItem;
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        return null;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order = baseMapper.selectOne(new QueryWrapper<OrderEntity>()
                .eq("order_sn", orderSn));
        return order;
    }

    @Override
    public void closeOrder(OrderEntity order) {
        // 关闭订单
        orderDao.closeOrder(order.getOrderSn(), OrderConstant.OrderStatusEnum.CLOSED.getStatus());
        // 解锁库存

    }

}