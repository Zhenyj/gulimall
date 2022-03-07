package com.zyj.gulimall.seckill.service;

import com.zyj.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author lulx
 * @date 2022-02-24 14:58
 **/

public interface SeckillService {
    /**
     * 上架最新3天的秒杀商品
     */
    void uploadSeckillSkuLates3Days();

    /**
     * 返回当前时间可以参与的秒杀商品信息
     *
     * @return
     */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 获取sku对应的秒杀信息
     * @param skuId
     * @return
     */
    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    /**
     * 秒杀商品
     * @param killId
     * @param key
     * @param num
     * @return
     */
    String kill(String killId, String key, Integer num);
}
