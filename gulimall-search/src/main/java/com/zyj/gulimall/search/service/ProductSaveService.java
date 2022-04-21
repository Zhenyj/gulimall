package com.zyj.gulimall.search.service;

import com.zyj.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author llx
 * @date 2021-11-02 22:14
 **/
public interface ProductSaveService {
    /**
     * 商品上架
     *
     * @param skuEsModels
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
