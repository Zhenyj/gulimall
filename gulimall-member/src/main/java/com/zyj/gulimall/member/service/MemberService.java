package com.zyj.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.to.SocialUser;
import com.zyj.common.to.UserLoginTo;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.member.entity.MemberEntity;
import com.zyj.gulimall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:56:03
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 登录
     *
     * @param to
     * @return
     */
    MemberEntity login(UserLoginTo to);

    /**
     * 注册
     *
     * @param vo
     * @return
     */
    void register(MemberRegisterVo vo);

    /**
     * 检测手机号唯一
     *
     * @param phone
     */
    void checkPhoneUnique(String phone);

    /**
     * 检测用户名唯一
     *
     * @param username
     */
    void checkUsernameUnique(String username);

    /**
     * 第三方授权登录
     *
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser);
}

