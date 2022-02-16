package com.zyj.common.exception;

/**
 * @author lulx
 * @date 2022-01-29 21:07
 **/
public class NoStockException extends RuntimeException {
    private Long skuId;

    public NoStockException(Long skuId) {
        super("商品id:" + skuId + ",库存不足");
    }

    public NoStockException(String msg) {
        super(msg);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
