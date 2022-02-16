package com.zyj.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyj.common.to.SkuHasStockVo;
import com.zyj.gulimall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStock(@Param("skuId") Long skuId);

    /**
     * 获取商品库存数据
     *
     * @param skuIds
     * @return
     */
    List<SkuHasStockVo> getSkuStockInfoBySkuIds(List<Long> skuIds);

    /**
     * 找到每个商品在哪个仓库有库存
     * @param skuId
     * @return
     */
    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    /**
     *
     * @param skuId
     * @param wareId
     * @param num
     * @return
     */
    int lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

    void unlockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
