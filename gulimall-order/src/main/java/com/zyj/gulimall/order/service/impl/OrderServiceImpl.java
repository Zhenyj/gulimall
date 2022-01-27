package com.zyj.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.common.utils.R;
import com.zyj.common.vo.MemberAddressVo;
import com.zyj.common.vo.MemberRespVo;
import com.zyj.common.vo.OrderItemVo;
import com.zyj.gulimall.order.dao.OrderDao;
import com.zyj.gulimall.order.entity.OrderEntity;
import com.zyj.gulimall.order.feign.CartFeignService;
import com.zyj.gulimall.order.feign.MemberFeignService;
import com.zyj.gulimall.order.interceptor.LoginUserInterceptor;
import com.zyj.gulimall.order.service.OrderService;
import com.zyj.gulimall.order.vo.OrderConfirmVo;
import com.zyj.gulimall.order.vo.OrderSubmitVo;
import com.zyj.gulimall.order.vo.PayVo;
import com.zyj.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

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
            R currentUserCartItems = cartFeignService.getCurrentUserCartItems();
            if (!Constant.SUCCESS_CODE.equals(currentUserCartItems.getCode())) {
                log.error(currentUserCartItems.getMsg());
                throw new RuntimeException(currentUserCartItems.getMsg());
            }
            List<OrderItemVo> orderItemVos = currentUserCartItems.getData(new TypeReference<List<OrderItemVo>>() {
            });
            orderConfirmVo.setItems(orderItemVos);
        }, executor);

        // 3、查询用户积分
        Long integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        CompletableFuture.allOf(memberAddressFuture, itemsFuture).get();

        return orderConfirmVo;
    }

    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        return null;
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        return null;
    }

}