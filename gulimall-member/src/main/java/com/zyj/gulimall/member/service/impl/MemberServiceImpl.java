package com.zyj.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.to.SocialUser;
import com.zyj.common.to.UserLoginTo;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.gulimall.member.dao.MemberDao;
import com.zyj.gulimall.member.entity.MemberEntity;
import com.zyj.gulimall.member.entity.MemberLevelEntity;
import com.zyj.gulimall.member.exception.PhoneExistException;
import com.zyj.gulimall.member.exception.UsernameExistException;
import com.zyj.gulimall.member.service.MemberLevelService;
import com.zyj.gulimall.member.service.MemberService;
import com.zyj.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberDao memberDao;

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>());

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {
        MemberEntity member = new MemberEntity();
        //默认等级
        MemberLevelEntity memberLevel = memberLevelService.getDefaultLevel();
        if (member != null) {
            member.setLevelId(memberLevel.getId());
        }
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUsername());

        member.setMobile(vo.getPhone());
        member.setUsername(vo.getUsername());
        member.setNickname(vo.getUsername());

        //加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        member.setPassword(encode);

        baseMapper.insert(member);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        int count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        int count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(UserLoginTo to) {
        String loginAccount = to.getLoginAccount();
        String password = to.getPassword();
        MemberEntity member = memberDao.selectMemberByLoginAccount(loginAccount);
        if (member == null) {
            return null;
        }
        String passwordDb = member.getPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // 密码匹配
        boolean matches = passwordEncoder.matches(password, passwordDb);
        if (matches) {
            return member;
        }
        return null;
    }

    @Override
    public MemberEntity login(SocialUser socialUser) {
        MemberEntity memberDb = baseMapper.selectOne(
                new QueryWrapper<MemberEntity>().eq("social_uid", socialUser.getId()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expiresIn = df.format(calendar.getTime());
        if (memberDb != null) {
            // 授权登录过
            MemberEntity member = new MemberEntity();
            member.setId(memberDb.getId());
            member.setAccessToken(socialUser.getAccess_token());
            member.setExpiresIn(expiresIn);
            baseMapper.updateById(member);
            memberDb.setAccessToken(socialUser.getAccess_token());
            member.setExpiresIn(expiresIn);
            return memberDb;
        } else {
            // 未授权，创建新账号
            MemberEntity member = new MemberEntity();

            if (StringUtils.hasText(socialUser.getName())) {
                member.setNickname(socialUser.getName());
            }

            if (StringUtils.hasText(socialUser.getAccess_token())) {
                member.setAccessToken(socialUser.getAccess_token());
            }

            if (socialUser.getId() != null) {
                member.setSocialUid(socialUser.getId().toString());
            }

            if (StringUtils.hasText(expiresIn)) {
                member.setExpiresIn(expiresIn);
            }

            if (StringUtils.hasText(socialUser.getAvatar_url())) {
                member.setHeader(socialUser.getAvatar_url());
            }
            if (StringUtils.hasText(socialUser.getEmail())) {
                member.setEmail(socialUser.getEmail());
            }

            baseMapper.insert(member);
            return member;
        }
    }
}