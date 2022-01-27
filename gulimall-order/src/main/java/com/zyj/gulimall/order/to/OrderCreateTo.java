package com.zyj.gulimall.order.to;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.zyj.gulimall.order.entity.OrderEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lulx
 * @date 2022-01-26 15:20
 **/
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItem> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}