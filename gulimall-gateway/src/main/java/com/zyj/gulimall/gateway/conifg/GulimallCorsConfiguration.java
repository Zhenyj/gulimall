package com.zyj.gulimall.gateway.conifg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author zyj
 * @create 2021-08-18 23:46
 * <p>
 * 跨域配置
 */

@Configuration
public class GulimallCorsConfiguration {

    /**
     * 跨域配置
     *
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1、配置跨域
        corsConfiguration.addAllowedHeader("*");    // 允许那些头进行跨域
        corsConfiguration.addAllowedMethod("*");    // 运行那些请求方式跨域
        // 较旧版本SpringBoot
        //corsConfiguration.addAllowedOrigin("*");    // 任意请求来源进行跨域
        // 较新版本SpringBoot
        corsConfiguration.addAllowedOriginPattern("*");     // 任意请求来源进行跨域
        corsConfiguration.setAllowCredentials(true);    // 是否允许携带cookie进行跨域


        source.registerCorsConfiguration("/**", corsConfiguration); //任意路径都进行跨域

        CorsWebFilter corsWebFilter = new CorsWebFilter(source);
        return corsWebFilter;
    }
}
