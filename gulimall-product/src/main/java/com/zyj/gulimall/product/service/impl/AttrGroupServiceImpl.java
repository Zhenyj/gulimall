package com.zyj.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.gulimall.product.dao.AttrGroupDao;
import com.zyj.gulimall.product.entity.AttrEntity;
import com.zyj.gulimall.product.entity.AttrGroupEntity;
import com.zyj.gulimall.product.service.AttrGroupService;
import com.zyj.gulimall.product.service.AttrService;
import com.zyj.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.zyj.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        IPage<AttrGroupEntity> page = null;
        String key = (String) params.get("key");
        // SELECT * FROM pms_attr_group where catelog_id=? and (attr_group_id=key or attr_group_name LIKE %key%)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (StringUtils.hasText(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if (catelogId == 0) {
            page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
        } else {
            page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper.eq("catelog_id", catelogId));
        }
        return new PageUtils(page);

    }

    /**
     * 获取分类下所有分组&关联属性
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 2、查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        // 1、查询当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        List<SkuItemVo.SpuItemAttrGroupVo> vo = attrGroupDao.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        return vo;
    }
}