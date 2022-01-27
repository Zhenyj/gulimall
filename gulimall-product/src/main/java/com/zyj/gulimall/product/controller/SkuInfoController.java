package com.zyj.gulimall.product.controller;

import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.R;
import com.zyj.gulimall.product.entity.SkuInfoEntity;
import com.zyj.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * sku信息
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @PostMapping("/infos")
    public R getSkuInfoBySkuIds(@RequestParam("skuIds") List<Long> skuIds) {
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuInfoBySkuIds(skuIds);
        return R.ok().setData(skuInfoEntities);
    }

    @PostMapping("/prices")
    public R getSkuPriceBySkuIds(@RequestParam("skuIds") List<Long> skuIds) {
        List<BigDecimal> prices = skuInfoService.getSkuPriceBySkuIds(skuIds);
        return R.ok().setData(prices);
    }

    @GetMapping("/{skuId}/price")
    public R getSkuPrice(@PathVariable("skuId") Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        return R.ok().setData(skuInfoEntity.getPrice());
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds) {
        skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
