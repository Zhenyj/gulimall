package com.zyj.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lulx
 * @date 2022-03-02 23:35
 **/
@Data
public class SeckillOrderTo {
    /** 订单号 */
    private String orderSn;
    /** 场次Id */
    private Long promotionSessionId;
    /** 商品id */
    private Long skuId;
    /** 秒杀价格 */
    private BigDecimal seckillPrice;
    /** 购买数量 */
    private Integer num;
    /** 会员id */
    private Long memberId;
}
