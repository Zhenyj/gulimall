package com.zyj.gulimall.product.feign;

import com.zyj.common.to.es.SkuEsModel;
import com.zyj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author llx
 * @date 2021-11-02 22:54
 **/
@FeignClient("gulimall-search")
public interface SearchFeignService {

    /**
     * 上架商品
     *
     * @param skuEsModels
     * @return
     */
    @PostMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
