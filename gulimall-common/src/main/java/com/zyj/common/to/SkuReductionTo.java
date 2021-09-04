package com.zyj.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zyj
 * @create 2021-09-04 13:17
 */
@Data
public class SkuReductionTo {

    public Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
