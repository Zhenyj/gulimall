package com.zyj.gulimall.order.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author lulx
 * @date 2022-01-26 19:52
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    R getAddressByMemberId(@PathVariable("memberId") Long memberId);
}
