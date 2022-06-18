package com.pzhu.substitute.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.OrderStatus;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.config.WebSocketServer;
import com.pzhu.substitute.entity.*;
import com.pzhu.substitute.entity.dto.DriverCertifyDTO;
import com.pzhu.substitute.entity.dto.LoginDTO;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;
import com.pzhu.substitute.mq.producer.Producer;
import com.pzhu.substitute.service.CommonService;
import com.pzhu.substitute.service.DriverService;
import com.pzhu.substitute.service.OrderService;
import com.pzhu.substitute.utils.MailUtil;
import com.pzhu.substitute.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dengyiqing
 * @description 驾驶员管理
 * @date 2022/2/8
 */
@RestController
@RequestMapping("driver")
@Api(value = "驾驶员模块", tags = {"驾驶员操作接口"})
@Slf4j
public class DriverController {
    private final MailUtil mailUtil;
    private final RedisUtil redisUtil;
    private final Producer springProducer;
    private final OrderService orderService;
    private final CommonService commonService;
    private final DriverService driverService;

    @Autowired
    public DriverController(MailUtil mailUtil, RedisUtil redisUtil, Producer springProducer, OrderService orderService, CommonService commonService, DriverService driverService) {
        this.mailUtil = mailUtil;
        this.redisUtil = redisUtil;
        this.springProducer = springProducer;
        this.orderService = orderService;
        this.commonService = commonService;
        this.driverService = driverService;
    }


    @PostMapping("login")
    @ApiOperation("代驾员登录")
    public Result login(@ApiParam(name = "登录DTO", value = "传入带账号密码的json格式", readOnly = true)
                        @RequestBody LoginDTO driver) {
        log.debug(driver.getPhoneNum(), driver.getPassword());
        commonService.checkLoginCode(driver.getUuid(), driver.getCode());
        driver.setLoginRule(CommonConstants.DRIVER_ROLE);
        return commonService.login(driver);
    }

    @GetMapping("detail")
    @ApiOperation("获得登录代驾员基本信息")
    public Result getDetail(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        DriverInfo driverInfo = principal.getDriverInfo();
        return Result.ok().data("driverInfo", driverInfo);
    }

    @GetMapping("balance")
    @ApiOperation("获得代驾员余额")
    public Result balance(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        DriverInfo driverInfo = principal.getDriverInfo();
        DriverBalance driverBalance = driverService.queryBalance(driverInfo);
        Double balance = driverBalance == null ? null : driverBalance.getBalance();
        return Result.ok().data("driverInfo", driverInfo).data("balance", balance);
    }

    @PostMapping("register")
    @ApiOperation("注册驾驶员账号")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return driverService.register(userRegisterDTO);
    }

    @PostMapping("certify")
    @ApiOperation("认证为驾驶员")
    public Result certify(@RequestBody DriverCertifyDTO driverCertifyDTO, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        return driverService.certify(driverCertifyDTO, principal.getDriverInfo());
    }

    @PostMapping("completeOrder/{orderId}")
    @ApiOperation("完成订单")
    public Result completeTheOrder(@PathVariable Long orderId, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        return driverService.completeTheOrder(orderId, principal.getDriverInfo());
    }

    @GetMapping("canTakeOrder")
    @ApiOperation("判断驾驶员是否能接单")
    public Result canTakeOrder(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        DriverInfo driverInfo = principal.getDriverInfo();
        boolean flag = driverService.canTakeOrder(driverInfo.getId());
        return Result.ok().data("canTakeOrder", flag);
    }

    @GetMapping("nowOrder")
    @ApiOperation("获得当前订单信息")
    public Result nowOrder(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        // todo 之后认真完善
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        DriverInfo driverInfo = principal.getDriverInfo();
        String redisKey = CommonConstants.DRIVER_PREFIX + driverInfo.getId() + CommonConstants.ORDER_PREFIX;
        Order nowOrder = (Order) redisUtil.get(redisKey);
        String orderUserKey = CommonConstants.USER_PREFIX + nowOrder.getUserId() + CommonConstants.LOGIN_SUFFIX;
        LoginUser loginUser = (LoginUser) redisUtil.get(orderUserKey);
        UserInfo userInfo = loginUser.getUserInfo();
        return Result.ok().data("nowOrder", nowOrder).data("userInfo", userInfo);
    }

    @GetMapping("iorders")
    @ApiOperation("获得我的全部订单")
    public Result iorders(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        DriverInfo driverInfo = principal.getDriverInfo();
        List<Order> orders = driverService.getMyOrder(driverInfo);
        return Result.ok().data("orderList", orders);
    }

    @GetMapping("availableOrders")
    @ApiOperation("查询全部可抢订单")
    @PreAuthorize("hasRole('driver')")
    public Result availableOrders() {
        Map<Object, Object> map = orderService.availableOrders();
        return Result.ok().data(map);
    }

    @PostMapping("receiveOrder")
    @ApiOperation("代驾员接收订单")
    @PreAuthorize("hasRole('driver')")
    public Result takeOrder(Long orderId, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        DriverInfo driverInfo = principal.getDriverInfo();
        orderService.takeOrder(orderId, driverInfo);
        return Result.ok();
    }

    @GetMapping("/pageAvailableOrder")
    @ApiOperation(value = "分页查可下单订单")
    public Result selectAll(Page<Order> page) {
        Page<Order> pageInfo = orderService.page(page,
                new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, OrderStatus.ASSIGNED));
        return Result.ok().data("pageInfo", pageInfo);
    }

    @RequestMapping("/push/{toUserId}")
    public ResponseEntity<String> pushToWeb(String message, @PathVariable String toUserId) throws IOException {
        WebSocketServer.sendInfo(message, toUserId);
        return ResponseEntity.ok("MSG SEND SUCCESS");
    }
}
