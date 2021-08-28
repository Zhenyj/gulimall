package com.zyj.gulimall.product.service.impl;

import com.zyj.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;

import com.zyj.gulimall.product.dao.CategoryDao;
import com.zyj.gulimall.product.entity.CategoryEntity;
import com.zyj.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    //@Autowired
    //CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage (Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 三级分类菜单
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree () {
        // 1、查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2、组装成树形结构
        List<CategoryEntity> levelMenus = entities.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> {
            menu.setChildren(getChildren(menu, entities));
            return menu;
        }).sorted((menu1,menu2)->{
            // 排序，避免空指针
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        //collect(Collectors.toList())表示收集流数据并转换为List
        return levelMenus;
    }

    /**
     * 递归查找所有菜单的子菜单
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            // 1、递归查找子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            // 2、排序，避免空指针
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 批量删除菜单
     * @param asList
     */
    @Override
    public void removeMenuByIds (List<Long> asList) {
        //TODO 1、检查当前册除的菜单，是否被别的地方引用

        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath (Long catelogId) {
        List<Long> paths = new ArrayList<>();
        findParentPath(paths, catelogId);
        // 逆序,[父,子,孙]
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }

    // [孙,子,父]
    private void findParentPath(List<Long> paths,Long catelogId){
        // 1、查找父id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid()!=0){
            findParentPath(paths, byId.getParentCid());
        }
    }

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade (CategoryEntity category) {
        this.updateById(category);
        if(StringUtils.hasText(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        }
    }
}