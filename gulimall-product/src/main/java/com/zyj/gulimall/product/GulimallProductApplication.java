package com.zyj.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients(basePackages = {"com.zyj.gulimall.product.feign"})
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main (String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
