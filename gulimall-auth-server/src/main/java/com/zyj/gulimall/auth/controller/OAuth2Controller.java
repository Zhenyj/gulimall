package com.zyj.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zyj.common.constant.AuthConstant;
import com.zyj.common.to.SocialUser;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.R;
import com.zyj.gulimall.auth.feign.MemberFeginService;
import com.zyj.gulimall.auth.utils.HttpUtils;
import com.zyj.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lulx
 * @date 2022-01-21 20:33
 **/
@Slf4j
@Controller
public class OAuth2Controller {

    private static final String CLIENT_ID = "aefcad3452b35be18a0077683e3dc5697564d53e48de0da1d09e91d44dfe9a59";
    private static final String CLIENT_SECRET = "f0aef12afe0058fa8f710a707721bd51e628d708f607ada6e47e42b648dc58d4";
    private static final String REDIRECT_URI = "http://auth.gulimall.com/oauth/gitee/success";
    private static final String LOGIN_URI = "http://auth.gulimall.com/login.html";

    @Autowired
    MemberFeginService memberFeginService;

    /**
     * gitee授权登录，可以参考gitee OAuth文档
     *
     * @param code
     * @param redirectAttributes
     * @param session
     * @return
     * @throws Exception
     */
    @GetMapping("/oauth/gitee/success")
    public String gitee(@RequestParam("code") String code, RedirectAttributes redirectAttributes, HttpSession session) throws Exception {
        //1、根据授权成功返回的code换取access_token
        Map<String, String> bodys = new HashMap<>(5);
        Map<String, String> headers = new HashMap<>(10);
        bodys.put("grant_type", "authorization_code");
        bodys.put("code", code);
        bodys.put("client_id", CLIENT_ID);
        bodys.put("redirect_uri", REDIRECT_URI);
        bodys.put("client_secret", CLIENT_SECRET);
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", headers, null, bodys);
        if (response.getStatusLine().getStatusCode() == Constant.SUCCESS_CODE) {
            String json = EntityUtils.toString(response.getEntity());
            String access_token = JSONObject.parseObject(json).getString("access_token");
            Map<String, String> querys = new HashMap<>(1);
            querys.put("access_token", access_token);
            // https://gitee.com/api/v5/user?access_token={access_token}
            HttpResponse userInfoResponse = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", headers, querys);
            if (userInfoResponse.getStatusLine().getStatusCode() == Constant.SUCCESS_CODE) {
                String userInfoJson = EntityUtils.toString(userInfoResponse.getEntity());
                SocialUser socialUser = JSON.parseObject(userInfoJson, SocialUser.class);
                R r = memberFeginService.oAuth2Login(socialUser);
                if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                    MemberRespVo data = r.getData(new TypeReference<MemberRespVo>() {
                    });
                    log.info("登录成功，用户:{}", data);
                    session.setAttribute(AuthConstant.LOGIN_USER, data);
                    session.setMaxInactiveInterval(60 * 60 * 24 * 30);
                    // TODO 解决session作用域问题，使子域名也可以共享session

                    // TODO 使用JSON序列化方式存储到redis中

                    return "redirect:http://gulimall.com";
                } else {
                    Map<String, String> errors = new HashMap<>(1);
                    errors.put("msg", r.getMsg());
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:" + LOGIN_URI;
                }
            } else {
                Map<String, String> errors = new HashMap<>(1);
                errors.put("msg", "第三方服务授权失败");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:" + LOGIN_URI;
            }
        } else {
            Map<String, String> errors = new HashMap<>(1);
            errors.put("msg", "第三方服务授权失败");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:" + LOGIN_URI;
        }
    }
}
