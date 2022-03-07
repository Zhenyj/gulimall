package com.zyj.gulimall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author llx
 * @date 2021-11-13 0:22
 **/
@Configuration
public class MyRedissonConfig {

    /**
     * 所有对Redisson的使用都是通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        // 创建配置useClusterServers
        Config config = new Config();
//        使用集群服务器
//        config.useClusterServers()
//                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");
        // 单节点模式useSingleServer
        config.useSingleServer().setAddress("redis://192.168.136.10:6379");
        // 创建redisson实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
