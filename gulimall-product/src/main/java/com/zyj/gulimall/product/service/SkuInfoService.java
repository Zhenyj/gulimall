package com.zyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.product.entity.SkuInfoEntity;
import com.zyj.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition (Map<String, Object> params);

    /**
     * 根据spuId查询sku信息
     * @param spuId
     * @return
     */
    List<SkuInfoEntity> getSkuBySpuId(Long spuId);

    /**
     * 获取商品详情
     * @param skuId skuId
     * @return
     */
    SkuItemVo getItem(Long skuId);
}

