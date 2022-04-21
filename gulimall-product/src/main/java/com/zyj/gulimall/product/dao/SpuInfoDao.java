package com.zyj.gulimall.product.dao;

import com.zyj.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu信息
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 修改spu上架状态
     *
     * @param spuId
     * @param code
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);

    /**
     * 根据skuId获取spu信息
     *
     * @param skuId
     * @return
     */
    SpuInfoEntity getSpuInfoBySkuId(@Param("skuId") Long skuId);

    List<SpuInfoEntity> getSpuInfoBySkuIds(List<Long> skuIds);
}
