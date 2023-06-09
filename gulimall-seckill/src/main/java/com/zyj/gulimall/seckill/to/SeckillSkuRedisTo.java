package com.zyj.gulimall.seckill.to;

import com.zyj.common.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lulx
 * @date 2022-02-24 20:14
 **/
@Data
public class SeckillSkuRedisTo {
    /** id */
    private Long id;

    /** 活动id */
    private Long promotionId;

    /** 活动场次id */
    private Long promotionSessionId;

    /** 商品id */
    private Long skuId;

    /** 秒杀价格 */
    private BigDecimal seckillPrice;

    /** 秒杀总量 */
    private Integer seckillCount;

    /** 每人限购数量 */
    private Integer seckillLimit;

    /** 排序 */
    private Integer seckillSort;

    /** 秒杀的开始时间 */
    private Long startTime;

    /** 秒杀的结束时间 */
    private Long endTime;

    /** 随机码 */
    private String randomCode;

    /** sku详细信息 */
    private SkuInfoVo skuInfo;
}
