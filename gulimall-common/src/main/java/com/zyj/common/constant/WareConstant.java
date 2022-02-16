package com.zyj.common.constant;

/**
 * @author zyj
 * @create 2021-09-06 23:46
 */
public class WareConstant {

    public enum PurchaseStatusEnum{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        RECEIVE(2,"已领取"),FINISH(3,"已完成"),HASERROR(4,"有异常");
        private int code;
        private String msg;

        PurchaseStatusEnum (int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode () {
            return code;
        }

        public String getMsg () {
            return msg;
        }
    }

    public enum PurchaseDetailStatusEnum{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),FINISH(3,"已完成"),HASERROR(4,"采购失败");
        private int code;
        private String msg;

        PurchaseDetailStatusEnum (int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode () {
            return code;
        }

        public String getMsg () {
            return msg;
        }
    }

    public enum LockStatusEnum {
        LOCKED(1,"已锁定"),
        UNLOCKED(2,"已解锁"),
        DEDUCTED(3,"已扣减");

        int status;
        String msg;

        LockStatusEnum(int status, String msg) {
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
