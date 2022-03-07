package com.zyj.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.to.mq.SeckillOrderTo;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.order.entity.OrderEntity;
import com.zyj.gulimall.order.vo.*;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:50:41
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 返回订单确认页需要的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 提交订单
     * @param vo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    PayVo getOrderPay(String orderSn);

    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 关闭订单
     * @param order
     */
    void closeOrder(OrderEntity order);

    /**
     * 处理支付
     * @param vo
     * @return
     */
    String handlePayResult(PayAsyncVo vo) throws ParseException;

    void updateOrderStatusByOrderSn(String orderSn,int status);

    /**
     * 分页查询
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 创建秒杀订单
     *
     * @param orderTo
     */
    void createSeckillOrder(SeckillOrderTo orderTo);
}

