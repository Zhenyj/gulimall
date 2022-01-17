package com.zyj.gulimall.search.constant;

/**
 * @author llx
 * @date 2021-11-02 22:21
 **/
public class EsConstant {

    /**
     * sku数据在es中的索引
     */
    public static final String PRODUCT_INDEX = "gulimall_product";
    /**
     * 有库存
     */
    public static final Integer HAS_STOCK = 1;
    /**
     * 没有库存
     */
    public static final Integer NO_STOCK = 0;

    public static final Integer PAGE_SIZE = 16;


}
