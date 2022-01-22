package com.zyj.gulimall.member.exception;

/**
 * @author lulx
 * @date 2022-01-19 21:29
 **/
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户已存在");
    }
}
