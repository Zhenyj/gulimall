package com.zyj.gulimall.seckill.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zyj.common.to.mq.SeckillOrderTo;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.R;
import com.zyj.common.vo.MemberRespVo;
import com.zyj.common.vo.SkuInfoVo;
import com.zyj.gulimall.seckill.feign.CouponFeignService;
import com.zyj.gulimall.seckill.feign.ProductFeignService;
import com.zyj.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.zyj.gulimall.seckill.service.SeckillService;
import com.zyj.gulimall.seckill.to.SeckillSkuRedisTo;
import com.zyj.gulimall.seckill.vo.SeckillSessionWithSkuVo;
import com.zyj.gulimall.seckill.vo.SeckillSkuVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author lulx
 * @date 2022-02-24 14:59
 **/
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";

    @Override
    public void uploadSeckillSkuLates3Days() {
        // 获取最新3天的秒杀活动
        R r = couponFeignService.getLates3DaySession();
        if (r.getCode().equals(Constant.SUCCESS_CODE)) {
            // 上架商品、放入缓存
            List<SeckillSessionWithSkuVo> sessionData = r.getData(new TypeReference<List<SeckillSessionWithSkuVo>>() {
            });
            if (sessionData != null && sessionData.size() > 0) {
                saveSessionInfos(sessionData);
                saveSessionSkuInfos(sessionData);
            }
        }
    }

    public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
        log.error("getCurrentSeckillSkusResource限流,{}", e.getMessage());
        return null;
    }

    @SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        // 1、获取当前时间
        long time = System.currentTimeMillis();
        log.info("获取当前时间:{}，秒杀场次商品信息", time);
        // Sentinel自定义受保护资源
        try (Entry entry = SphU.entry("seckillSkus")) {
            Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                Long start = Long.parseLong(s[0]);
                Long end = Long.parseLong(s[1]) + (4 * 60 * 60 * 1000);
                if (time >= start && time <= end) {
                    // 2、获取当前场次的商品信息
                    List<String> range = redisTemplate.opsForList().range(key, 0, -1);
                    BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = ops.multiGet(range);
                    if (list != null && list.size() > 0) {
                        List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
                            SeckillSkuRedisTo redisTo = JSON.parseObject(item, SeckillSkuRedisTo.class);
                            return redisTo;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }
            }
        } catch (BlockException e) {
            log.error("资源限流,{}", e.getMessage());
        }
        return null;
    }

    /**
     * 保存秒杀活动信息
     */
    private void saveSessionInfos(List<SeckillSessionWithSkuVo> sessions) {
        log.info("准备缓存商品秒杀活动信息");

        for (SeckillSessionWithSkuVo session : sessions) {
            String key = SESSIONS_CACHE_PREFIX + session.getStartTime().getTime() + "_" + session.getEndTime().getTime();
            if (!redisTemplate.hasKey(key)) {
                List<SeckillSkuVo> relationSkus = session.getRelationSkus();
                List<String> values = relationSkus.stream().map(sku -> sku.getPromotionSessionId() + "_" + sku.getSkuId()).collect(Collectors.toList());
                //缓存活动信息
                redisTemplate.opsForList().leftPushAll(key, values);
                log.info("商品秒杀活动信息缓存成功");
            }
        }
    }

    /**
     * 保存秒杀商品信息
     *
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SeckillSessionWithSkuVo> sessions) {
        for (SeckillSessionWithSkuVo session : sessions) {
            BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

            List<SeckillSkuVo> relationSkus = session.getRelationSkus();
            List<Long> skuIds = session.getRelationSkus().stream().map(seckillSkuVo -> {
                return seckillSkuVo.getSkuId();
            }).collect(Collectors.toList());
            if (skuIds == null || skuIds.size() == 0) {
                log.warn("每日秒杀活动活动:{},不存在秒杀商品信息", session.getId());
                continue;
            }
            log.info("远程调用商品服务获取每日秒杀活动:{}相关商品信息", session.getId());
            R r = productFeignService.getSkuInfoBySkuIds(skuIds);
            if (!Constant.SUCCESS_CODE.equals(r.getCode())) {
                throw new RuntimeException("远程调用商品服务获取每日秒杀活动id:" + session.getId() + "活动商品信息失败");
            }
            List<SkuInfoVo> skuInfoVoList = r.getData(new TypeReference<List<SkuInfoVo>>() {
            });
            if (skuInfoVoList == null || skuInfoVoList.size() == 0) {
                log.warn("每日秒杀活动id:{},没有活动商品", session.getId());
                continue;
            }
            Map<Long, SkuInfoVo> skuInfoMap = skuInfoVoList.stream().collect(Collectors.toMap(SkuInfoVo::getSkuId, Function.identity()));
            for (SeckillSkuVo seckillSkuVo : relationSkus) {
                // 设置随机码,为了保证是人为点击抢购商品,而不是脚本之类的预知url进行秒杀 seckill?skuId=1&key=xxxxx
                String randomCode = UUID.randomUUID().toString().replace("-", "");
                String skuKey = seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId();
                if (!ops.hasKey(skuKey)) {
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    // sku的基本数据
                    SkuInfoVo skuInfoVo = skuInfoMap.get(seckillSkuVo.getSkuId());
                    if (skuInfoVo == null) {
                        log.warn("缺少skuId:{}商品数据", seckillSkuVo.getSkuId());
                        continue;
                    }
                    redisTo.setSkuInfo(skuInfoVo);

                    // 2、sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);

                    // 3、设置秒杀时间
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    redisTo.setRandomCode(randomCode);

                    String s = JSON.toJSONString(redisTo);
                    ops.put(skuKey, s);
                }

                // 信号量设置为秒杀商品的数量
                String semaphoreKey = SKU_STOCK_SEMAPHORE + randomCode;
                if (!redisTemplate.hasKey(semaphoreKey)) {
                    RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }
            }
        }
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        List<String> sessionKeys = new ArrayList<>(redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*"));
        // 按开始时间排序，获取最近一次的秒杀活动价信息
        Collections.sort(sessionKeys, (String o1, String o2) -> {
            int i = (int) (Long.parseLong(o1.substring(17, 30)) - Long.parseLong(o2.substring(17, 30)));
            return i;
        });

        for (String sessionKey : sessionKeys) {
            List<String> range = redisTemplate.opsForList().range(sessionKey, 0, -1);
            for (String key : range) {
                String regx = "\\d_" + skuId;
                if (Pattern.matches(regx, key)) {
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    long current = System.currentTimeMillis();
                    if (current >= skuRedisTo.getStartTime() && current <= skuRedisTo.getEndTime()) {

                    } else {
                        //
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }
            }
        }
        return null;
    }

    /**
     * TODO 上架秒杀商品的时候，每一个数据都有过期时间
     * TODO 秒杀后续流程，收获地址等信息
     *
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo member = LoginUserInterceptor.loginUser.get();
        // 1、获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if (!StringUtils.hasText(json)) {
            return null;
        } else {
            SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
            //校验合法性
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = System.currentTimeMillis();
            long ttl = endTime - startTime;
            //校验时间合法性
            if (time >= startTime && time <= endTime) {
                //校验随机码和商品id
                String randomCode = redisTo.getRandomCode();
                String skuId = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
                if (randomCode.equals(key) && killId.equals(skuId)) {
                    //验证购物数量
                    if (num <= redisTo.getSeckillCount()) {
                        //验证是否购买过，幂等性。 只要秒杀成功，就去占位 userId_sessionId_skuId
                        String redisKey = member.getId() + "_" + skuId;
                        //自动过期
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, String.valueOf(num), ttl, TimeUnit.MILLISECONDS);
                        if (aBoolean != null && aBoolean) {
                            //占位成功：从来没有买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);

                            boolean b = semaphore.tryAcquire(num);
                            if (b) {
                                //秒杀成功
                                //快速下单，发送MQ消息
                                String orderSn = IdUtil.getSnowflake().nextIdStr();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setOrderSn(orderSn);
                                orderTo.setMemberId(member.getId());
                                orderTo.setNum(num);
                                orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                orderTo.setSkuId(redisTo.getSkuId());
                                orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                rabbitTemplate.convertAndSend("order.event.exchange", "order.seckill.order", orderTo);
                                return orderSn;
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return null;
    }
}
