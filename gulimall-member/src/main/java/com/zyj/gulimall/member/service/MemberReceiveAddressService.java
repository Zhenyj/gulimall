package com.zyj.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyj.common.utils.PageUtils;
import com.zyj.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:56:03
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取用户收货地址
     *
     * @param memberId
     * @return
     */
    List<MemberReceiveAddressEntity> getAddressByMemberId(Long memberId);
}

