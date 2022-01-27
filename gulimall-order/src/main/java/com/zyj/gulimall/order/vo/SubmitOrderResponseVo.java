package com.zyj.gulimall.order.vo;

import com.zyj.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author lulx
 * @date 2022-01-26 15:19
 **/
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code; //200成功
}
