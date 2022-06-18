package com.pzhu.substitute.controller;

import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.ResultCode;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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

    @Autowired
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

    @PostMapping("register")
    @ApiOperation("用户注册")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @GetMapping("logout")
    @ApiOperation("退出登录")
    public Result logout() {
        return userService.logout();
    }

    @GetMapping("detail")
    @ApiOperation("获得登录用户基本信息")
    public Result getDetail(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        UserInfo userInfo = principal.getUserInfo();
        return Result.ok().data("userInfo", userInfo);
    }

    @PutMapping("detail")
    @ApiOperation("修改用户基本信息")
    public Result updateDetail(@RequestBody UserInfoDTO userInfoDTO, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        return userService.updateBasicUserInfo(userInfoDTO, principal.getUserInfo());
    }

    @GetMapping("iorders")
    @ApiOperation("查询我的全部订单")
    public Result queryOrders(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        return userService.queryIOrders(principal.getUserInfo());
    }
    @GetMapping("nowOrder")
    @ApiOperation("查询我当前的订单")
    public Result nowOrder(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        return userService.queryNowOrder(principal.getUserInfo());
    }
    @GetMapping("comment")
    @ApiOperation("获得该订单的评论")
    public Result queryOrderComment(Long orderId, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        return userService.queryOrderComment(orderId, principal.getUserInfo());
    }

    @PostMapping("comment")
    @ApiOperation("对订单的评论")
    public Result queryOrders(Long orderId, String comment, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        return userService.commentOrder(orderId, comment);
    }

    @GetMapping("order/driver/{orderId}")
    @ApiOperation("查询该订单的代驾员信息")
    public Result queryOrderDriver(@PathVariable Long orderId, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        return userService.queryOrderDriver(orderId, principal.getUserInfo());
    }
}
