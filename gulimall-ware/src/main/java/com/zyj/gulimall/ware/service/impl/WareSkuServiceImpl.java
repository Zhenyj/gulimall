package com.zyj.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.constant.WareConstant;
import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.exception.NoStockException;
import com.zyj.common.to.SkuHasStockVo;
import com.zyj.common.to.mq.StockDetailTo;
import com.zyj.common.to.mq.StockLockedTo;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.common.utils.R;
import com.zyj.common.vo.OrderItemVo;
import com.zyj.common.vo.OrderVo;
import com.zyj.gulimall.ware.dao.WareSkuDao;
import com.zyj.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.zyj.gulimall.ware.entity.WareOrderTaskEntity;
import com.zyj.gulimall.ware.entity.WareSkuEntity;
import com.zyj.gulimall.ware.feign.OrderFeignService;
import com.zyj.gulimall.ware.feign.ProductFeignService;
import com.zyj.gulimall.ware.service.WareOrderTaskDetailService;
import com.zyj.gulimall.ware.service.WareOrderTaskService;
import com.zyj.gulimall.ware.service.WareSkuService;
import com.zyj.gulimall.ware.to.WareSkuLockTo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.hasText(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚 seata
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");

                if (info.getCode().equals(Constant.SUCCESS_CODE)) {
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {

            }
            wareSkuDao.insert(skuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }

    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = wareSkuDao.getSkuStockInfoBySkuIds(skuIds);
        if (skuIds.size() != skuHasStockVos.size()) {
            log.error(BizCodeEnum.PRODUCT_WARE_EXCEPTION.getMsg());
            throw new RuntimeException(BizCodeEnum.PRODUCT_WARE_EXCEPTION.getMsg());
        }
        return skuHasStockVos;
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }

    /**
     * 库存解锁场景
     * 1）、下单成功，订单过期、被用户手动取消、 需要解锁库存
     * 2）、下单成功，业务调用失败，导致订单回滚 之前锁定的库存需要解锁
     *
     * @param wareSkuLockTo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockTo wareSkuLockTo) {
        log.info("订单:{},锁定库存", wareSkuLockTo.getOrderSn());
        //保存库存工作单详情 追溯
        WareOrderTaskEntity task = new WareOrderTaskEntity();
        task.setOrderSn(wareSkuLockTo.getOrderSn());
        wareOrderTaskService.save(task);

        // 可以按照下单的收货地址判断就近仓库是否有库存，目前使用简单的逻辑代替
        List<OrderItemVo> locks = wareSkuLockTo.getLocks();
        List<SkuWareHasStock> skuWareHasStocks = locks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(item.getCount());
            // 找到每个商品在哪个仓库有库存
            log.info("获取skuId:{}商品，有库存的仓库", skuId);
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareIds(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock hasStock : skuWareHasStocks) {
            boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareIds();
            if (wareIds == null || wareIds.size() == 0) {
                log.warn("商品id:" + skuId + ",库存不足");
                throw new NoStockException(skuId);
            }
            //1、如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
            //2、锁定失败。前面保存信息就回滚了。
            // 防止回滚以后找不到数据
            for (Long wareId : wareIds) {
                // 成功返回1，否则返回0
                log.info("锁定商品skuId:{}，数量:{},仓库:{}", skuId, hasStock.getNum(), wareId);
                int count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count > 0) {
                    skuStocked = true;
                    WareOrderTaskDetailEntity taskDetail = new WareOrderTaskDetailEntity();
                    taskDetail.setSkuId(skuId);
                    taskDetail.setSkuName("");
                    taskDetail.setSkuNum(hasStock.getNum());
                    taskDetail.setTaskId(task.getId());
                    taskDetail.setWareId(wareId);
                    taskDetail.setLockStatus(1);
                    wareOrderTaskDetailService.save(taskDetail);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(task.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetail, detailTo);
                    lockedTo.setDetailTo(detailTo);
                    rabbitTemplate.convertAndSend("stock.event.exchange", "stock.locked", lockedTo);
                    break;
                } else {
                    //当前仓库锁失败 重试下一个仓库
                    log.warn("{}仓库商品:{}，库存不足", wareId, hasStock.getSkuId());
                }
            }
            if (!skuStocked) {
                log.warn("商品id:" + skuId + ",库存不足");
                throw new NoStockException(skuId);
            }
        }

        return true;
    }

    /**
     * 库存自动解锁
     * 库存解锁场景
     * 1）、下单成功，订单过期、被用户手动取消、 需要解锁库存
     * 2）、下单成功，业务调用失败，导致订单回滚 之前锁定的库存需要解锁
     *
     * @param to
     */
    @Transactional
    @Override
    public void unlockStock(StockLockedTo to) {
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();
        //关于锁定库存信息
        WareOrderTaskDetailEntity wareOrderTaskDetail = wareOrderTaskDetailService.getById(detailId);
        if (wareOrderTaskDetail != null) {
            //有，回滚库存
            Long id = to.getId();
            WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getById(id);
            String orderSn = wareOrderTask.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if (data == null || data.getStatus() == 4) {
                    //订单不存在或订单取消
                    //当前状态1才可以解锁
                    if (wareOrderTaskDetail.getLockStatus() == WareConstant.LockStatusEnum.LOCKED.getStatus()) {
                        unlockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                //消息拒绝以后,重新入队让其他继续解锁
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new RuntimeException("远程服务失败");
            }
        } else {
            // 不需要回滚
        }
    }

    /**
     * 解决订单服务卡顿库存无法解锁
     */
    @Transactional
    @Override
    public void unlockStock(OrderVo order) {
        String orderSn = order.getOrderSn();
        WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        if (task == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException();
        }
        Long taskId = task.getId();
        WareOrderTaskDetailEntity wareOrderTaskDetail = new WareOrderTaskDetailEntity();
        wareOrderTaskDetail.setTaskId(taskId);
        wareOrderTaskDetail.setLockStatus(1);
        List<WareOrderTaskDetailEntity> wareOrderTaskDetails = wareOrderTaskDetailService.getWareOrderTaskDetailList(wareOrderTaskDetail);
        for (WareOrderTaskDetailEntity taskDetail : wareOrderTaskDetails) {
            //当前状态1才可以解锁
            if (taskDetail.getLockStatus() == WareConstant.LockStatusEnum.LOCKED.getStatus()) {
                unlockStock(taskDetail.getSkuId(), taskDetail.getWareId(), taskDetail.getSkuNum(), taskDetail.getId());
            }
        }
    }


    private void unlockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        //库存解锁
        wareSkuDao.unlockStock(skuId, wareId, num);
        //更新库存工作单状态
        wareOrderTaskDetailService.unlockTaskDetail(taskDetailId, WareConstant.LockStatusEnum.UNLOCKED.getStatus());
    }
}