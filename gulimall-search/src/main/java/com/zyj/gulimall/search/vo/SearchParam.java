package com.zyj.gulimall.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 商品检索条件vo
 *
 * @author lulx
 * @date 2022-01-13 17:25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchParam {

    /** 匹配关键字 */
    private String keyword;
    /** 三级分类id */
    private Long catalog3Id;

    /**
     * 排序条件
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;

    /** 是否只显示有货 */
    private Integer hasStock;

    /**
     * 价格区间
     * skuPrice=1_100/_100/100_
     */
    private String skuPrice;

    /** 品牌id */
    private List<Long> brandId;

    /**
     * 商品属性
     * attrs=属性id_属性值
     */
    private List<String> attrs;

    /** 页码 */
    private Integer pageNum = 1;

    /** 原生所有查询属性 */
    private String queryString;
}
