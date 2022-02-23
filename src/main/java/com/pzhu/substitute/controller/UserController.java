package com.pzhu.substitute.controller;

import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

//    @GetMapping("login")
//    @ApiOperation("微信登录")
//    public Result login(String code){
//        return userService.wxLogin();
//    }

    @PostMapping("register")
    @ApiOperation("注册用户")
    public Result register(String username, String password, String code) {
        return userService.register(username, password, code);
    }

    @GetMapping("code")
    @ApiOperation("获得二维码")
    public Result code(String phoneNum) {
        return userService.createCode(phoneNum);
    }

    @GetMapping("logout")
    @ApiOperation("退出登录")
    public Result logout() {
        return userService.logout();
    }
}
