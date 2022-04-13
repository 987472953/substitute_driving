package com.pzhu.substitute.controller;

import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.ResultCode;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.entity.dto.LoginDTO;
import com.pzhu.substitute.entity.dto.UserInfoDTO;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;
import com.pzhu.substitute.service.CommonService;
import com.pzhu.substitute.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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

    private final UserService userService;
    private final CommonService commonService;

    public UserController(UserService userService, CommonService commonService) {
        this.userService = userService;
        this.commonService = commonService;
    }

    @PostMapping("login")
    @ApiOperation("用户登录")
    public Result login(@ApiParam(name = "用户对象", value = "传入带账号密码的json格式", readOnly = true)
                        @RequestBody LoginDTO user) {
        log.debug(user.getPhoneNum(), user.getPassword());
        commonService.checkLoginCode(user.getUuid(), user.getCode());
        user.setLoginRule(CommonConstants.USER_ROLE);
        return commonService.login(user);
    }

//    @GetMapping("login")
//    @ApiOperation("微信登录")
//    public Result login(String code){
//        return userService.wxLogin();
//    }

    @PostMapping("register")
    @ApiOperation("注册用户")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @GetMapping("logout")
    @ApiOperation("退出登录")
    public Result logout() {
        return userService.logout();
    }

    @GetMapping("detail")
    @ApiOperation("获得用户基本信息")
    public Result getDetail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            UserInfo userInfo = ((LoginUser) principal).getUserInfo();
            return Result.ok().data("userInfo", userInfo);
        } else {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
    }

    @PutMapping("detail")
    @ApiOperation("修改用户信息")
    public Result updateDetail(@RequestBody UserInfoDTO userInfoDTO, Authentication authentication) {
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        if (principal != null && principal.getUserInfo() != null) {
            return userService.updateBasicUserInfo(userInfoDTO, principal.getUserInfo());
        } else {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
    }
}
