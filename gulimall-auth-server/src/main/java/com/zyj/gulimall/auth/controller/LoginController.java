package com.zyj.gulimall.auth.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import com.zyj.common.constant.AuthConstant;
import com.zyj.common.to.UserLoginTo;
import com.zyj.common.to.UserRegisterTo;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.R;
import com.zyj.gulimall.auth.feign.MemberFeginService;
import com.zyj.gulimall.auth.vo.UserLoginVo;
import com.zyj.gulimall.auth.vo.UserRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lulx
 * @date 2022-01-19 12:45
 **/
@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeginService memberFeginService;

    /**
     * //TODO 重定向携带数据，利用session原理。将数据放在session中。
     * 只要跳到下一个页面取出这个数据以后，session里面的数据就会删除掉
     * //TODO 1、分布式下的session问题。
     * <p>
     * redirectAttributes 模拟重定向携带数据
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //校验失败 去注册页
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                if (!errors.containsKey(fieldError.getField())) {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
            }
            // 重定向携带数据
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //1、校验验证码
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (StringUtils.isNotEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                //删除验证码
                redisTemplate.delete(AuthConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //验证码通过 真正注册 远程服务
                UserRegisterTo to = new UserRegisterTo();
                BeanUtils.copyProperties(vo, to);
                R r = memberFeginService.register(to);
                if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                    //成功
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>(1);
                    errors.put("msg", r.getMsg());
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                Map<String, String> errors = new HashMap<>(1);
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes) {
        //远程登录
        UserLoginTo to = new UserLoginTo();
        BeanUtils.copyProperties(vo, to);
        R r = memberFeginService.login(to);
        if (Constant.SUCCESS_CODE.equals(r.getCode())) {
            //成功
            UserLoginTo data = r.getData(new TypeReference<UserLoginTo>() {
            });
            redirectAttributes.addFlashAttribute(AuthConstant.LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getMsg());
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
