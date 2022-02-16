package com.zyj.gulimall.order.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author lulx
 * @date 2022-01-29 16:45
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/skuId/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/spuinfo/skuIds}")
    R getSpuInfoBySkuIds(@RequestParam("skuIds") List<Long> skuIds);
}
