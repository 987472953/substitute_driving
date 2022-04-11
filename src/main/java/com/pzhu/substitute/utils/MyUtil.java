package com.pzhu.substitute.utils;

import org.springframework.util.StringUtils;

/**
 * @author dengyiqing
 * @description 自定义工具类
 * @date 2022/4/10
 */
public class MyUtil {
    public static boolean checkIsNotNull(String... arr) {
        for (String s : arr) {
            if (!StringUtils.hasText(s)) return false;
        }
        return true;
    }
}
