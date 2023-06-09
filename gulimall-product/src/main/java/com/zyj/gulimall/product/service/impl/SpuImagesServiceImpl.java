package com.zyj.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;

import com.zyj.gulimall.product.dao.SpuImagesDao;
import com.zyj.gulimall.product.entity.SpuImagesEntity;
import com.zyj.gulimall.product.service.SpuImagesService;
import org.springframework.transaction.annotation.Transactional;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>());

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveImages(Long id, List<String> images) {
        if (images == null || images.size() == 0) {
            return;
        }
        List<SpuImagesEntity> collect = images.stream().map(img -> {
            SpuImagesEntity imagesEntity = new SpuImagesEntity();
            imagesEntity.setSpuId(id);
            imagesEntity.setImgUrl(img);
            return imagesEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}