package com.zyj.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.common.utils.R;
import com.zyj.common.vo.FareVo;
import com.zyj.common.vo.MemberAddressVo;
import com.zyj.gulimall.ware.dao.WareInfoDao;
import com.zyj.gulimall.ware.entity.WareInfoEntity;
import com.zyj.gulimall.ware.feign.MemberFeignService;
import com.zyj.gulimall.ware.service.WareInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    WareInfoDao wareInfoDao;

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wareInfoEntityQueryWrapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        R r = memberFeignService.getMemberReceiveAddress(addrId);
        if (!Constant.SUCCESS_CODE.equals(r.getCode())) {
            log.error("远程获取收货地址信息异常，收货地址id:{}", addrId);
            throw new RuntimeException("收货地址信息异常");
        }
        MemberAddressVo memberAddressVo = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        FareVo fareVo = new FareVo();
        fareVo.setAddress(memberAddressVo);

        // TODO 根据运费规则进行计算运费
        // 这里使用手机号最后一位作为运费
        String phone = memberAddressVo.getPhone();
        String s = phone.substring(phone.length() - 2);
        BigDecimal fare = new BigDecimal(s);
        fareVo.setFare(fare);
        return fareVo;
    }

}