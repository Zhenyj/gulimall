package com.zyj.gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lulx
 * @date 2022-02-24 14:54
 * <p>
 * 秒杀商品定时上架：
 * 每天晚上3点;上架最近三天需要秒杀的商品。
 **/
@Configuration
@EnableScheduling
public class ScheduledConfig {


}
