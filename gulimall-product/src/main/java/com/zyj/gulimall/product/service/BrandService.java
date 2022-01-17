package com.zyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 更新品牌信息
     *
     * @param brand
     */
    void updateDetail(BrandEntity brand);

    /**
     * 获取品牌信息
     *
     * @param brandIds
     * @return
     */
    List<BrandEntity> getBrandsByIds(List<Long> brandIds);
}