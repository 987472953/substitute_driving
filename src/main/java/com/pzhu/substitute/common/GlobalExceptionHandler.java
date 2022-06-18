package com.pzhu.substitute.common;

import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.utils.MailUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
 * @author dengyiqing
 * @description 全局异常处理
 * @date 2022/1/14
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MailUtil mailUtil;
    private Date email_limit_date;

    @Autowired
    public GlobalExceptionHandler(MailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public Result bizExceptionHandler(HttpServletRequest req, BizException e) {
        log.error("发生业务异常！原因是：{}", e.getErrorMsg());
        return Result.error(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public Result bizExceptionHandler(HttpServletRequest req, AccessDeniedException e) {
        log.error("发生业务异常！原因是：{}", e.getMessage());
        return Result.error(4001, e.getMessage());
    }

    /**
     * 处理空指针的异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, NullPointerException e) {
        log.error("发生空指针异常！原因是:", e);
        return Result.error(ResultCode.BODY_NOT_MATCH);
    }

    /**
     * 处理用户登录过期
     */
    @ExceptionHandler(value = ExpiredJwtException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, ExpiredJwtException e) {
        log.error("jwt凭证过期:", e);
        return Result.error(ResultCode.JWT_EXPIRED);
    }

    /**
     * 处理用户被禁用
     */
    @ExceptionHandler(value = DisabledException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, DisabledException e) {
        log.error("用户被禁用:", e);
        return Result.error(ResultCode.DISABLE).message("用户被禁用, 请联系管理员");
    }


    /**
     * 处理用户被锁定
     */
    @ExceptionHandler(value = LockedException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, LockedException e) {
        log.error("用户被锁定:", e);
        return Result.error(ResultCode.LOCKED).message("用户被锁定, 请联系管理员");
    }

    /**
     * 处理密码错误
     */
    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, BadCredentialsException e) {
        log.error("用户名或密码错误:", e);
        return Result.error(ResultCode.REDIS_ERROR).message("缓存连接异常, 请等待工作人员修复");
    }

    /**
     * Redis连接异常
     */
    @ExceptionHandler(value = RedisConnectionFailureException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, RedisConnectionFailureException e) {
        log.error("未知异常！原因是:", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("未知异常！原因是:", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理MQ异常
     */
    @ExceptionHandler(value = MQException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, MQException e) {
        if (email_limit_date == null || new Date().getTime() - email_limit_date.getTime() > 30 * 60 * 1000) {
            email_limit_date = new Date();
            String infoText = "MQ消息队列 发生异常\nmessage: [%s]\ncause: [%s]\nstackTrace: \n%s";
            String format = String.format(infoText, e.getMessage(), e.getCause(), Arrays.toString(e.getStackTrace()));
            mailUtil.sendMessage("RocketMQ 异常", format, "987472953@qq.com");
        }
        log.error("消息队列异常, 原因是:", e);

        return Result.error(ResultCode.MQ_FAILED_CONSUME_MESSAGE);
    }
}
