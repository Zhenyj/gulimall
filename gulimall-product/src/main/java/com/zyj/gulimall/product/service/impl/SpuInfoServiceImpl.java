package com.zyj.gulimall.product.service.impl;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.zyj.common.to.SkuReductionTo;
import com.zyj.common.to.SpuBoundsTo;
import com.zyj.common.utils.R;
import com.zyj.gulimall.product.entity.AttrEntity;
import com.zyj.gulimall.product.entity.ProductAttrValueEntity;
import com.zyj.gulimall.product.entity.SkuImagesEntity;
import com.zyj.gulimall.product.entity.SkuInfoEntity;
import com.zyj.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.zyj.gulimall.product.entity.SpuImagesEntity;
import com.zyj.gulimall.product.entity.SpuInfoDescEntity;
import com.zyj.gulimall.product.feign.CouponFeignService;
import com.zyj.gulimall.product.service.AttrService;
import com.zyj.gulimall.product.service.ProductAttrValueService;
import com.zyj.gulimall.product.service.SkuImagesService;
import com.zyj.gulimall.product.service.SkuInfoService;
import com.zyj.gulimall.product.service.SkuSaleAttrValueService;
import com.zyj.gulimall.product.service.SpuImagesService;
import com.zyj.gulimall.product.service.SpuInfoDescService;
import com.zyj.gulimall.product.vo.Attr;
import com.zyj.gulimall.product.vo.BaseAttrs;
import com.zyj.gulimall.product.vo.Bounds;
import com.zyj.gulimall.product.vo.Images;
import com.zyj.gulimall.product.vo.Skus;
import com.zyj.gulimall.product.vo.SpuSaveVo;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;

import com.zyj.gulimall.product.dao.SpuInfoDao;
import com.zyj.gulimall.product.entity.SpuInfoEntity;
import com.zyj.gulimall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage (Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo (SpuSaveVo vo) {
        // 1、保存spu基本信息，pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2、保存spu的描述，pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        // spuId因为是自增主键会在上面保存基本数据后，又赋值给spuInfoEntity对象，所以这里拿得到生成的spuId
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",", decript)); // 使用,隔开进行拼接
        spuInfoDescService.save(descEntity);

        // 3、保存spu的图片集，pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 4、保存spu的基本参数，pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> attrs = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(attrs);

        // 6、保存spu的积分信息：gulimall-sms数据库sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu的积分信息失败");
        }

        // 5、保存spu对应的所有sku信息
        // 5.1、sku的基本信息，pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                // 5.2、sku的图片信息，pms_sku_images
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    return StringUtils.hasText(entity.getImgUrl());
                }).collect(Collectors.toList());
                // TODO 没有路径的无需保存
                skuImagesService.saveBatch(imagesEntities);

                // 5.3、sku的销售属性，pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 5.4、sku的优惠、满减信息，gulimall-sms数据库中sms_sku_ladder、sms_sku_full_reduction、sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(spuInfoEntity.getId());
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
    }

    @Transactional
    @Override
    public void saveBaseSpuInfo (SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }
}