package com.zyj.gulimall.cart.config;

import com.zyj.gulimall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lulx
 * @date 2022-01-19 14:32
 **/
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
