package com.zyj.common.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lulx
 * @date 2022-01-29 0:23
 **/
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
