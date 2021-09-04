package com.zyj.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zyj
 * @create 2021-09-04 13:02
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
