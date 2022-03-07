package com.zyj.gulimall.member.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author lulx
 * @date 2022-02-21 14:37
 **/
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @RequestMapping("order/order/listWithItem")
    R listWithItem(@RequestParam Map<String, Object> params);
}
