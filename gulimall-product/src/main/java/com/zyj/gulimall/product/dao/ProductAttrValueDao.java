package com.zyj.gulimall.product.dao;

import com.zyj.gulimall.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu属性值
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:32
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    /**
     * 删除spu属性
     * @param spuId
     */
    void deleteBySpuId(@Param("spuId") Long spuId);
}
