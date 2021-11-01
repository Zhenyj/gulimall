package com.zyj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.product.entity.AttrEntity;
import com.zyj.gulimall.product.vo.AttrGroupRelationVo;
import com.zyj.gulimall.product.vo.AttrRespVo;
import com.zyj.gulimall.product.vo.AttrVo;

import java.util.List;
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

    List<AttrEntity> getRelationAttr (Long attrgroupId);

    void deleteRelation (AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr (Map<String, Object> params, Long attrgroupId);

    /**
     * 在所有属性即合理，筛选出检索属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

