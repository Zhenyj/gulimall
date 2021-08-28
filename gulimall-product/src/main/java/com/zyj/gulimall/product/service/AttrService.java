package com.zyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.product.entity.AttrEntity;
import com.zyj.gulimall.product.vo.AttrRespVo;
import com.zyj.gulimall.product.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr (AttrVo attr);

    PageUtils queryBaseAttrPage (Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo getAttrInfo (Long attrId);

    void updateAttr (AttrVo attr);
}

