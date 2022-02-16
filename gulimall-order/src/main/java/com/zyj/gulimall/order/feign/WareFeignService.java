package com.zyj.gulimall.order.feign;

import com.zyj.common.utils.R;
import com.zyj.gulimall.order.to.WareSkuLockTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author lulx
 * @date 2022-01-27 18:39
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestParam List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    public R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockTo wareSkuLockTo);
}
