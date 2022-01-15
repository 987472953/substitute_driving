package com.pzhu.substitute.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author dengyiqing
 * @description 使用到的常量
 * @date 2022/1/15
 */
@Component
public class CommonConstants {
    public static String JWT_SALT;

    @Value("${constant.jwt.salt}")
    public void setJwtSalt(String salt) {
        CommonConstants.JWT_SALT = salt;
    }

    public static final String REQUEST_RESOURCES_PERMIT_ALL = "/swagger-ui.html,/webjars/**,/swagger-resources/**,/v2/**";
    public static final String REQUEST_ANONYMOUS = "/user/login";

    // Redis
    public static final String USER_PREFIX = "user:";
    public static final String LOGIN_SUFFIX = ":login";

}
