package com.zyj.gulimall.search.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author lulx
 * @date 2022-01-15 23:55
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @RequestMapping("/product/attr/info/{attrId}")
    public R getAttrInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    public R getBrandInfo(@RequestParam("brandIds") List<Long> brandIds);
}
