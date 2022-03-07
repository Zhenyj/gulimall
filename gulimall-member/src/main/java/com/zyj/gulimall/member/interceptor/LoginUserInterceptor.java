package com.zyj.gulimall.member.interceptor;

import com.zyj.common.constant.AuthConstant;
import com.zyj.gulimall.member.entity.MemberEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author lulx
 * @date 2022-02-21 15:07
 **/
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberEntity> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberEntity member = (MemberEntity) session.getAttribute(AuthConstant.LOGIN_USER);
        if (member != null) {
            loginUser.set(member);
            return true;
        } else {
            session.setAttribute("msg", "请先进行登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
