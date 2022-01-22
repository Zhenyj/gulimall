package com.zyj.gulimall.auth.controller;

import com.zyj.common.constant.AuthConstant;
import com.zyj.common.utils.R;
import com.zyj.gulimall.auth.feign.ThirdPartFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lulx
 * @date 2022-01-19 16:45
 **/
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        if (StringUtils.isEmpty(phone)) {
            return R.error("手机号不能为空");
        }
        //TODO 1. 接口防刷
//        String redisCode = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + phone);
//        if (StringUtils.isNotEmpty(redisCode)) {
//            long l = Long.parseLong(redisCode.split("_")[1]);
//            if (System.currentTimeMillis() - l < 60000) {
//                //60秒不能再发
//                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
//            }
//        }
        //2. redis 存 key-phone value-code
        String code = UUID.randomUUID().toString().substring(0, 5);
        code = "1234";
//        R r = thirdPartFeignService.sendCode(phone, code);
        //验证码_系统时间，时间用于防止某段时间内多次请求
        code = code + "_" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        return R.ok();
    }
}
