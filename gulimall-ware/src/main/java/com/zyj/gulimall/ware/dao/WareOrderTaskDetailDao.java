package com.zyj.gulimall.ware.dao;

import com.zyj.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存工作单
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Mapper
public interface WareOrderTaskDetailDao extends BaseMapper<WareOrderTaskDetailEntity> {

    void unlockTaskDetail(@Param("id") Long id, @Param("status") int status);

    List<WareOrderTaskDetailEntity> getWareOrderTaskDetailList(WareOrderTaskDetailEntity wareOrderTaskDetail);
}
