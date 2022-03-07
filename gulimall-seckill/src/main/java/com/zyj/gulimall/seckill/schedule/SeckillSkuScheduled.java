package com.zyj.gulimall.seckill.schedule;

import com.zyj.gulimall.seckill.service.SeckillService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author lulx
 * @date 2022-02-24 14:53
 **/
@Service
public class SeckillSkuScheduled {
    @Autowired
    SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";


    @Scheduled(cron = "0 * * * * ?")
    public void uploadSeckillSkuLates3Days() {
        // 1、重复上架无需处理
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);
        //保证操作的原子性，防止其他服务也进行操作
        try {
            seckillService.uploadSeckillSkuLates3Days();
        } finally {
            lock.unlock();
        }
    }
}
