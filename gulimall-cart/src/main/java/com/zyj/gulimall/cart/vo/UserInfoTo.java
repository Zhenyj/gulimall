package com.zyj.gulimall.cart.vo;

import lombok.Data;

/**
 * @author lulx
 * @date 2022-01-23 0:20
 **/
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser;
}
