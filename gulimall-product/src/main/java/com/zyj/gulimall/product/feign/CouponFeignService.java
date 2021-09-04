package com.zyj.gulimall.product.feign;

import com.zyj.common.to.SkuReductionTo;
import com.zyj.common.to.SpuBoundsTo;
import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zyj
 * @create 2021-09-04 12:51
 */
@FeignClient("gulimall-coupon")
@Service
public interface CouponFeignService {

    /**
     * 1、CouponFeignService.saveSpuBounds( spuBoundTo);
     *      1) 、@RequestBody将这个对象转为json。
     *      2)、找到guLimall-coupon服务，给/coupon/spubounds/save发送请求。将上一步转的json放在请求体位置,发送请求;
     *      3) 、对方服务收到请求。请求体里有json数据。
     *      (@RequestBody SpuBoundsEntity spuBounds)﹔将请求体的json转为SpuBoundsEntity;
     * 只要json数据模型是兼容的。双方服务无需使用同一个to
     *
     * @param spuBoundsTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds (@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction (@RequestBody SkuReductionTo skuReductionTo);
}
