package com.zyj.gulimall.product.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author llx
 * @date 2021-11-01 22:41
 **/
@FeignClient(name = "gulimall-ware")
public interface WareFeignService {

    /**
     * 如何设计返回数据
     * 1、R设计的时候可以加上泛型,并添加一个泛型的data属性，用于返回数据，好处
     * 2、直接返回结果
     * 3、自己封装结果集,例如公共vo放在公共服务里
     * 这里使用第一种方法
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
