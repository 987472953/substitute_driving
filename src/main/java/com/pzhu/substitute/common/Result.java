package com.pzhu.substitute.common;

import com.pzhu.substitute.common.status.ResultCode;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dengyiqing
 * @description 统一返回结果
 * @date 2022/1/10
 */
@Data
public class Result {

    private boolean success;//是否成功
    private Integer code;// 返回码
    private String message;//返回信息
    private Map<Object, Object> data = new HashMap<>();// 返回数据

    public Result(ResultCode code) {
        this.success = code.success();
        this.code = code.code();
        this.message = code.message();
    }

    public Result(ResultCode code, Map<Object, Object> data) {
        this.success = code.success();
        this.code = code.code();
        this.message = code.message();
        this.data = data;
    }

    public Result(Integer code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public static Result error() {
        return new Result(ResultCode.FAIL);
    }

    public static Result error(ResultCode resultCode) {
        return new Result(resultCode);
    }

    public static Result error(Integer errorCode, String errorMsg) {
        return new Result(errorCode, errorMsg, false);
    }

    public static Result ok() {
        return new Result(ResultCode.SUCCESS);
    }

    public static Result ok(Map<Object, Object> data) {
        return new Result(ResultCode.SUCCESS, data);
    }

    public static Result ok(Map<Object, Object> data, String message) {
        return new Result(ResultCode.SUCCESS, data).message(message);
    }

    public static Result ok(ResultCode resultCode) {
        return new Result(resultCode);
    }

    public Result data(Map<Object, Object> data) {
        this.setData(data);
        return this;
    }

    public Result data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Result message(String message) {
        this.setMessage(message);
        return this;
    }

    public Result code(Integer code) {
        this.setCode(code);
        return this;
    }

    public Result success(boolean isSuccess) {
        this.setSuccess(isSuccess);
        return this;
    }
}
