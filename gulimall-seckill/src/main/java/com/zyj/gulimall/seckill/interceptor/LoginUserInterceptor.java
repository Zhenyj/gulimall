package com.zyj.gulimall.seckill.interceptor;

import com.zyj.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lulx
 * @date 2022-03-02 21:57
 **/
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    public static final String LOGIN_USER = "loginUser";

    /**
     * 用户登录检测
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 拦截匹配的请求路径
        if (!uri.matches("^/kill.*$")) {
            return true;
        }
        MemberRespVo member = (MemberRespVo) request.getSession().getAttribute(LOGIN_USER);
        if (member == null) {
            // 没登录
            request.getSession().setAttribute("msg", "请先进行登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
        loginUser.set(member);
        return true;
    }
}
