package com.zyj.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.gulimall.ware.dao.WareOrderTaskDetailDao;
import com.zyj.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.zyj.gulimall.ware.service.WareOrderTaskDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Autowired
    WareOrderTaskDetailDao wareOrderTaskDetailDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<WareOrderTaskDetailEntity> getWareOrderTaskDetailList(WareOrderTaskDetailEntity wareOrderTaskDetail) {
        List<WareOrderTaskDetailEntity> entities = wareOrderTaskDetailDao.getWareOrderTaskDetailList(wareOrderTaskDetail);
        return entities;
    }

    @Override
    public void unlockTaskDetail(Long id, int status) {
        wareOrderTaskDetailDao.unlockTaskDetail(id, status);
    }
}