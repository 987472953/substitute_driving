package com.pzhu.substitute.controller;

import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengyiqing
 * @description 用户模块
 * @date 2022/1/10
 */
@RestController
@RequestMapping("user")
@Api(value = "用户模块", tags = {"用户操作接口"})
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    @ApiOperation("用户登录")
    public Result login(@ApiParam(name = "用户对象", value = "传入带账号密码的json格式", readOnly = true)
                        @RequestBody UserInfo user) {
        log.debug(user.getPhoneNum(), user.getPassword());
        return userService.login(user.getPhoneNum(), user.getPassword());
    }
}
