package com.zyj.gulimall.ware.controller;

import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.to.SkuHasStockVo;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.R;
import com.zyj.gulimall.ware.entity.WareSkuEntity;
import com.zyj.gulimall.ware.service.WareSkuService;
import com.zyj.gulimall.ware.to.WareSkuLockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品库存
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:56:03
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 锁定库存
     *
     * 库存解锁的场景：
     * 1）、下单成功、订单过期未支付、取消订单
     *
     * @param wareSkuLockTo
     * @return
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockTo wareSkuLockTo) {
        try {
            Boolean b = wareSkuService.orderLockStock(wareSkuLockTo);
            return R.ok().setData(b);
        } catch (Exception e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }
    }

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestParam List<Long> skuIds) {
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
