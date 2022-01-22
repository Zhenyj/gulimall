package com.zyj.gulimall.auth.feign;

import com.zyj.common.to.SocialUser;
import com.zyj.common.to.UserLoginTo;
import com.zyj.common.to.UserRegisterTo;
import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author lulx
 * @date 2022-01-19 21:14
 **/
@FeignClient("gulimall-member")
public interface MemberFeginService {


    @PostMapping("/member/member/register")
    public R register(@RequestBody UserRegisterTo to);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginTo to);

    @PostMapping("/member/member/oauth2/login")
    public R oAuth2Login(@RequestBody SocialUser socialUser);
}
