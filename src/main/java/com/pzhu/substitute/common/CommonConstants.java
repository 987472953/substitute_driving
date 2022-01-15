package com.pzhu.substitute.common;

/**
 * @author dengyiqing
 * @description 使用到的常量
 * @date 2022/1/15
 */
public interface CommonConstants {
    String REQUEST_RESOURCES_PERMIT_ALL = "/swagger-ui.html,/webjars/**,/swagger-resources/**,/v2/**";
    String REQUEST_ANONYMOUS = "/user/login";

    // Redis
    String USER_PREFIX = "user:";
    String LOGIN_SUFFIX = ":login";

}
