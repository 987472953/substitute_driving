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
    public static final String SPLIT_REGEX = ":";
    public static String JWT_SALT;
    public static final String JWT_HEADER = "Authentication";


    @Value("${constant.jwt.salt}")
    public void setJwtSalt(String salt) {
        CommonConstants.JWT_SALT = salt;
    }

    public static final String REQUEST_RESOURCES_PERMIT_ALL = "/swagger-ui.html,/webjars/**,/swagger-resources/**,/v2/**";
    public static final String REQUEST_ANONYMOUS = "/user/login,/user/register,/user/code";

    public static String USER_ROLE = "user";
    public static String DRIVER_ROLE = "driver";

    // Redis
    public static final String ORDER_SUFFIX = ":tradeCode";
    public static final String USER_PREFIX = "user:";
    public static final String LOGIN_SUFFIX = ":login";
    public static final String REGISTER_CODE = "register:";
    public static final String ORDER_LIST = "order:list";
    public static final String LOGIN_IMAGE_CODE_PREFIX = "image:";
    public static final String LOGIN_IMAGE_CODE_SUFFIX = ":code";

    // 价格
    public static final String PRICE_TO_PAY = "实际待支付价格";
    public static final String DISTANCE_PRICE = "距离费用";
    public static final String COUPON_DISCOUNT = "优惠券减免";
}
