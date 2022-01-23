package com.zyj.gulimall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lulx
 * @date 2022-01-18 15:52
 **/
@Component
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
public class ThreadPoolConfigProperties {
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer keepAliveTime;
}
