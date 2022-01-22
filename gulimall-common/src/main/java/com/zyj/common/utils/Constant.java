/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.zyj.common.utils;

/**
 * 常量
 */
public class Constant {
    /** 超级管理员ID */
    public static final int SUPER_ADMIN = 1;
    /** 当前页码 */
    public static final String PAGE = "page";
    /** 每页显示记录数 */
    public static final String LIMIT = "limit";
    /** 排序字段 */
    public static final String ORDER_FIELD = "sidx";
    /** 排序方式 */
    public static final String ORDER = "order";
    /** 升序 */
    public static final String ASC = "asc";

    /** 成功 */
    public static final Integer SUCCESS_CODE = 200;

    /** 失败 */
    public static final Integer FAIL_CODE = 500;

    /**
     * 菜单
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 定时任务状态
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
