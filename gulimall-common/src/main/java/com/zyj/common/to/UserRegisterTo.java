package com.zyj.common.to;

import lombok.Data;

/**
 * @author lulx
 * @date 2022-01-19 20:50
 **/
@Data
public class UserRegisterTo {
    private String username;
    private String password;
    private String phone;
}
