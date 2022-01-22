package com.zyj.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyj.gulimall.member.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:56:03
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
    /**
     * 通过登录账号获取用户
     *
     * @param loginAccount
     * @return
     */
    MemberEntity selectMemberByLoginAccount(@Param("loginAccount") String loginAccount);
}
