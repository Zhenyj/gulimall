package com.zyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.zyj.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取spu销售属性组合
     *
     * @param spuId spuId
     * @return
     */
    List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);

    /**
     * 根据skuId获取销售属性键值对字符串列表
     *
     * @param skuId
     * @return
     */
    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

