package com.pzhu.substitute.common;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dengyiqing
 * @description 全局异常处理
 * @date 2022/1/14
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义的业务异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public Result bizExceptionHandler(HttpServletRequest req, BizException e) {
        log.error("发生业务异常！原因是：{}", e.getErrorMsg());
        return Result.error(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, NullPointerException e) {
        log.error("发生空指针异常！原因是:", e);
        return Result.error(ResultCode.BODY_NOT_MATCH);
    }

    /**
     * 处理用户登录过期
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = ExpiredJwtException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, ExpiredJwtException e) {
        log.error("jwt凭证过期:", e);
        return Result.error(ResultCode.JWT_EXPIRED);
    }

    /**
     * 处理用户被禁用
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = DisabledException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, DisabledException e) {
        log.error("用户被禁用:", e);
        return Result.error(ResultCode.DISABLE).message("用户被禁用, 请联系管理员");
    }


    /**
     * 处理用户被锁定
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = LockedException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, LockedException e) {
        log.error("用户被锁定:", e);
        return Result.error(ResultCode.LOCKED).message("用户被锁定, 请联系管理员");
    }

    /**
     * 处理密码错误
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, BadCredentialsException e) {
        log.error("用户名或密码错误:", e);
        return Result.error(ResultCode.LOCKED).message("用户名或密码错误");
    }

    /**
     * 处理其他异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("未知异常！原因是:", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }
}
