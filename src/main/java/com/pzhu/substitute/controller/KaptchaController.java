package com.pzhu.substitute.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.utils.RedisUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * @author dengyiqing
 * @description 验证码管理
 * @date 2022/3/26
 */
@RestController
@Api(value = "验证码管理", tags = {"验证码工具接口"})
public class KaptchaController {
    /**
     * 验证码工具
     */
    @Autowired
    DefaultKaptcha defaultKaptcha;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/kaptcha/sessionId")
    public Result sessionId(HttpServletRequest request) {
        return Result.ok().data("info", request.getSession().getId());
    }

    @GetMapping("/defaultKaptcha")
    public void defaultKaptcha(String uuid, HttpServletResponse response) throws Exception {
        byte[] captcha;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            String createText = defaultKaptcha.createText();
            String loginImageKey = CommonConstants.LOGIN_IMAGE_CODE_PREFIX + uuid + CommonConstants.LOGIN_SUFFIX;
            redisUtil.set(loginImageKey, createText, 2 * 60 * 60);
            BufferedImage bi = defaultKaptcha.createImage(createText);
            ImageIO.write(bi, "jpg", out);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        captcha = out.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream sout = response.getOutputStream();
        sout.write(captcha);
        sout.flush();
        sout.close();
    }
}