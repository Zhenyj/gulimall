package com.zyj.gulimall.product.dao;

import com.zyj.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
