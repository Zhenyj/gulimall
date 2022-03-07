package com.zyj.gulimall.order.constant;

/**
 * @author lulx
 * @date 2022-01-26 15:16
 **/
public class OrderConstant {
    public static final String USER_ORDER_TOKEN_PREFIX = "order:token:";

    public enum SubmitOrderResp {
        /** 订单提交成功 */
        SUCCESS(200, "订单提交成功"),
        /** 超时 */
        TIME_OUT_ERROR(500, "订单信息过期，请刷新再次提交"),
        /** 价格错误 */
        PRICE_ERROR(501, "订单商品价格发生变化，请确认后再次提交"),
        /** 库存错误 */
        STOCK_ERROR(502, "库存锁定失败，库存商品不足");

        /** 状态码 */
        private int code;
        /** 信息 */
        private String msg;

        SubmitOrderResp(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }
    }

    public enum OrderStatusEnum {
        CREATE_NEW(0, "代付款"),
        PAYED(1, "已付款"),
        DELIVERED(2, "已发货"),
        COMPLETED(3, "已完成"),
        CANCEL(4, "已取消"),
        SERVICING(5, "售后中"),
        SERVICED(6, "售后完成");
        int status;
        String msg;

        OrderStatusEnum(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }
    }
}
