package com.zyj.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author lulx
 * @date 2022-01-19 20:53
 **/
@Data
public class UserRegisterVo {
    @NotBlank(message = "用户名必须填写")
    @Length(min = 3, max = 16, message = "用户名长度只能在3-16个字符之间")
    private String username;

    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    @NotBlank(message = "密码必须填写")
    private String password;

    @NotBlank(message = "手机号必须填写")
    @Pattern(regexp = "^0?(13|14|15|18|17)[0-9]{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "验证码必须填写")
    private String code;
}
