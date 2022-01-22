package com.zyj.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:56:03
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取用户默认等级
     * @return
     */
    MemberLevelEntity getDefaultLevel();

}

