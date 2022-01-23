package com.zyj.gulimall.cart.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lulx
 * @date 2022-01-23 0:13
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R getSkuInfo(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/skuinfo/info/{id}")
    public R info(@PathVariable("id") Long id);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    public R getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skuinfo/{skuId}/price")
    public R getSkuPrice(@PathVariable("skuId") Long skuId);
}
