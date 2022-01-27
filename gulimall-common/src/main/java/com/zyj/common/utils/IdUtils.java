package com.zyj.common.utils;

import java.util.UUID;

/**
 * @author lulx
 * @date 2022-01-26 19:34
 **/
public class IdUtils {

    public static String fastSimpleUUID() {
        return UUID.randomUUID().toString();
    }
}
