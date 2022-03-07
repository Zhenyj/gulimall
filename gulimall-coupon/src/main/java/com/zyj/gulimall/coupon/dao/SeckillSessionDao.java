package com.zyj.gulimall.coupon.dao;

import com.zyj.gulimall.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 秒杀活动场次
 * 
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:43:29
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {

    List<SeckillSessionEntity> getLates3DaySession();
}
