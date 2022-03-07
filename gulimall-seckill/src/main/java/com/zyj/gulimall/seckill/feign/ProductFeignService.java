package com.zyj.gulimall.seckill.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author lulx
 * @date 2022-02-24 21:01
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @PostMapping("/product/skuinfo/infos")
    public R getSkuInfoBySkuIds(@RequestParam("skuIds") List<Long> skuIds);
}
