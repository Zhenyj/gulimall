package com.zyj.gulimall.ware.to;

import com.zyj.common.vo.OrderItemVo;
import lombok.Data;

import java.util.List;

/**
 * @author lulx
 * @date 2022-01-29 20:27
 **/
@Data
public class WareSkuLockTo {
    private String orderSn;
    private List<OrderItemVo> locks;
}
