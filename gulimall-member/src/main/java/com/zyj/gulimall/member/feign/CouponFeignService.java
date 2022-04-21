package com.zyj.gulimall.member.feign;

import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zyj
 * @create 2021-08-05 22:48
 */

@FeignClient("gulimall-coupon") // 远程客户端调用某服务,即调用gulimall-coupon服务
@Service
public interface CouponFeignService {

    /**
     * 在注册中心中先找远程服务，在调用请求
     *
     * @return
     */
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
