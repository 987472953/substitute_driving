package com.pzhu.substitute.common;

/**
 * @author dengyiqing
 * @description 自定义业务异常
 * @date 2022/1/14
 */
public class MQException extends RuntimeException {

    private static final long serialVersionUID = 6049018190940560888L;
    /**
     * 错误码
     */
    protected Integer errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public MQException() {
        super();
    }

    public MQException(ResultCode resultCode) {
        super(resultCode.message);
        this.errorCode = resultCode.code;
        this.errorMsg = resultCode.message;
    }

    public MQException(ResultCode resultCode, Throwable cause) {
        super(resultCode.message, cause);
        this.errorCode = resultCode.code;
        this.errorMsg = resultCode.message;
    }

    public MQException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public MQException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public MQException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public MQException setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public MQException setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public String getMessage() {
        return errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}

