package com.pzhu.substitute.common;

/**
 * @author dengyiqing
 * @description 操作返回码
 * @date 2022/1/10
 */
public enum ResultCode {

    SUCCESS(true, 2000, "操作成功！"),
    //---系统错误返回码-----
    FAIL(false, 10001, "操作失败"),
    LOGIN_FAIL(false, 4001, "登录失败"),
    UNAUTHENTICATED(false, 10002, "您还未登录"),
    UNAUTHORISE(false, 10003, "权限不足"),
    SERVER_ERROR(false, 99999, "抱歉，系统繁忙，请稍后重试！"),
    BODY_NOT_MATCH(false, 4000, "请求的数据格式不符!"),
    INTERNAL_SERVER_ERROR(false, 5000, "服务器内部错误!");

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
