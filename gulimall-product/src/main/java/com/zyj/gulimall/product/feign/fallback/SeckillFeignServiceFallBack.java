package com.zyj.gulimall.product.feign.fallback;

import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.utils.R;
import com.zyj.gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lulx
 * @date 2022-03-07 16:02
 **/
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("秒杀服务降级");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
