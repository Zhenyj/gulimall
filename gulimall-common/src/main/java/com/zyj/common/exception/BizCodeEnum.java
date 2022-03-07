package com.zyj.common.exception;

/**
 * @author zyj
 * @create 2021-08-23 1:15
 * <p>
 * 错误码和错误信息定义类
 * 1。错误码定义规则为5为数字
 * 2，前两位表示业务场景，最后三位表示错误码。例如:100001。10:通用 001:系统未知异常
 * 3．维护错误码后需要维护错误描述,将他们定义为枚举形式
 * 错误码列表:
 * 10:通用
 * 001:参数格式校验
 * 11:商品
 * 12:订单
 * 13:购物车
 * 14:物流
 */

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VAILD_EXCEPTION(10001, "参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，请稍后再试"),
    TOO_MANY_REQUEST(10003, "请求流量过大,请稍后再试"),
    DEGRADE(10004, "服务降级"),
    PARAM_FLOW(10005, "参数限流"),
    SYSTEM_BLOCK(10006, "系统负载异常"),
    AUTHORITY(10007, "授权异常"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    PRODUCT_CATEGORY_EXCEPTION(11001, "商品分类异常"),
    PRODUCT_WARE_EXCEPTION(11002, "商品库存异常"),
    CART_PRODUCT_INFO_EXCEPTION(13001, "购物车商品信息缺失、不完整"),
    USER_EXIST_EXCEPTION(15001, "用户已存在"),
    PHONE_EXIST_EXCEPTION(15002, "手机号已存在"),
    LOGIN_ACCT_PASSWORD_INVALID_EXCEPTION(15003, "账号密码错误"),
    NO_STOCK_EXCEPTION(21000, "商品库存不足");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
