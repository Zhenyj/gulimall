package com.zyj.gulimall.member.exception;

/**
 * @author lulx
 * @date 2022-01-19 21:28
 **/
public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("手机号存在");
    }
}