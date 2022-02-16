package com.zyj.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.to.SkuHasStockVo;
import com.zyj.common.to.mq.StockLockedTo;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.vo.OrderVo;
import com.zyj.gulimall.ware.entity.WareSkuEntity;
import com.zyj.gulimall.ware.to.WareSkuLockTo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);


    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 为订单锁定库存
     *
     * @param wareSkuLockTo
     * @return
     */
    Boolean orderLockStock(WareSkuLockTo wareSkuLockTo);

    void unlockStock(OrderVo order);

    void unlockStock(StockLockedTo to);
}

