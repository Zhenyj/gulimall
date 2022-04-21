package com.zyj.gulimall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyj.gulimall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:50:41
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void closeOrder(@Param("orderSn") String orderSn, @Param("status") int status);

    void updateOrderStatusByOrderSn(@Param("orderSn") String orderSn, @Param("status") int status);
}
