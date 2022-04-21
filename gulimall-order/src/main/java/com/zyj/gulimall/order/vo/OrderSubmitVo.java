package com.zyj.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lulx
 * @date 2022-01-26 15:18
 **/
@Data
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;
    //商品从服务中重新获取
    //优惠 发票

    private String token;
    /** 应付金额 */
    private BigDecimal payPrice;
    /** 订单备注 */
    private String note;
    /** 令牌 */
    private String orderToken;
}
