package com.zyj.gulimall.ware.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lulx
 * @date 2022-01-28 11:05
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R getMemberReceiveAddress(@PathVariable("id") Long id);
}
