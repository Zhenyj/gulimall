package com.zyj.gulimall.order.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lulx
 * @date 2022-01-27 0:14
 **/
@FeignClient("gulimall-cart")
public interface CartFeignService {

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    R getCurrentUserCartItems();
}
