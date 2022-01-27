package com.zyj.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.order.entity.OrderEntity;
import com.zyj.gulimall.order.vo.OrderConfirmVo;
import com.zyj.gulimall.order.vo.OrderSubmitVo;
import com.zyj.gulimall.order.vo.PayVo;
import com.zyj.gulimall.order.vo.SubmitOrderResponseVo;

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
}

