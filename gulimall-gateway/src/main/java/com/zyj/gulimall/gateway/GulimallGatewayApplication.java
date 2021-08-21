package com.zyj.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.sql.DataSource;

/**
 * 1、开启服务注册发现
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallGatewayApplication {

    public static void main (String[] args) {
        SpringApplication.run(GulimallGatewayApplication.class, args);
    }

}
