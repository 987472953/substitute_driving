package com.pzhu.substitute.controller;

import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengyiqing
 * @description TODO
 * @date 2022/1/10
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody UserInfo user) {
        log.debug(user.getPhoneNum(), user.getPassword());
        return userService.login(user.getPhoneNum(), user.getPassword());
    }
}
