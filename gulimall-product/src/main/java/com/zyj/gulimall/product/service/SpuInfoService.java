package com.zyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.product.entity.SpuInfoDescEntity;
import com.zyj.gulimall.product.entity.SpuInfoEntity;
import com.zyj.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo (SpuSaveVo vo);

    void saveBaseSpuInfo (SpuInfoEntity spuInfoEntity);
}

