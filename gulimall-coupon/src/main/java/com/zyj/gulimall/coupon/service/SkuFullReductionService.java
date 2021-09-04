package com.zyj.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.to.SkuReductionTo;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:43:29
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction (SkuReductionTo skuReductionTo);
}

