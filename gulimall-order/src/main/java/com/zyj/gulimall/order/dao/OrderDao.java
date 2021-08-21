package com.zyj.gulimall.order.dao;

import com.zyj.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:50:41
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
