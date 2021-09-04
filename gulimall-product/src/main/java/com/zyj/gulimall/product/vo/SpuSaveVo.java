/**
 * Copyright 2021 json.cn
 */
package com.zyj.gulimall.product.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-09-03 18:45:6
 */
@Data
public class SpuSaveVo {

    @NotNull
    private String spuName;
    private String spuDescription;

    @NotNull
    private Long catalogId;
    @NotNull
    private Long brandId;
    private BigDecimal weight;

    @NotNull
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}