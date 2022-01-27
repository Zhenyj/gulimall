package com.zyj.gulimall.cart.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lulx
 * @date 2022-01-23 0:13
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/skuinfo/info/{id}")
    R info(@PathVariable("id") Long id);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    R getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skuinfo/{skuId}/price")
    R getSkuPrice(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/skuinfo/infos")
    R getSkuInfoBySkuIds(@RequestParam("skuIds") List<Long> skuIds);
}
