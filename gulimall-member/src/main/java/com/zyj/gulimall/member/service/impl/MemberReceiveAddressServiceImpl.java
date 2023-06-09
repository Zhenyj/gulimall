package com.zyj.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.gulimall.member.dao.MemberReceiveAddressDao;
import com.zyj.gulimall.member.entity.MemberReceiveAddressEntity;
import com.zyj.gulimall.member.service.MemberReceiveAddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>());

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getAddressByMemberId(Long memberId) {
        return baseMapper.selectList(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));
    }

}