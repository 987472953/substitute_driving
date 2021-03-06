package com.pzhu.substitute.common.status;

/**
 * @author dengyiqing
 * @description 操作返回码
 * @date 2022/1/10
 */
public enum ResultCode {

    SUCCESS(true, 2000, "操作成功！"),
    //---系统错误返回码-----
    FAIL(false, 10001, "操作失败"),
    BIZ_FAIL(false, 10000000, "业务异常,请确保正常操作页面"),
    CODE_ERROR(false, 10000000, "登录验证码错误,或验证码过期"),
    CREATED(true, 4001, "创建成功"),
    CODE_INCORRECT(false, 4006, "验证码错误"),
    LOGIN_FAIL(false, 4002, "登录失败"),
    DISABLE(false, 4003, "用户被禁用"),
    USER_DOES_NOT_EXIST(false, 4004, "用户不存在"),
    USER_ALREADY_EXIST(false, 4005, "用户已存在"),
    LOCKED(false, 4004, "用户被锁定"),
    BAD_CREDENTIALS(false, 4005, "密码凭证错误"),
    UNAUTHENTICATED(false, 10002, "您还未登录"),
    JWT_EXPIRED(false, 10003, "您的登录已过期"),
    UNAUTHORISE(false, 10003, "权限不足"),
    SERVER_ERROR(false, 99999, "抱歉，系统繁忙，请稍后重试！"),
    BODY_NOT_MATCH(false, 4000, "请求的数据格式不符!"),
    INTERNAL_SERVER_ERROR(false, 5000, "服务器内部错误!"),
    REDIS_ERROR(false, 5001, "服务器缓存异常!"),
    ORDER_EXPIRED(false, 4006, "订单过期"),
    ORDER_ERROR(false, 4006, "订单问题"),
    MQ_FAILED_CONSUME_MESSAGE(false, 40016, "消费者消息失败");

    //---用户操作返回码----
    //---企业操作返回码----
    //---权限操作返回码----
    //---其他操作返回码----

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    ResultCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
