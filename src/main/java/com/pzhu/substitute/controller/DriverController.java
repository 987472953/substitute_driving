package com.pzhu.substitute.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.OrderStatus;
import com.pzhu.substitute.config.WebSocketServer;
import com.pzhu.substitute.entity.Order;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

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
    @ApiOperation("驾驶员登录")
    public Result login(@ApiParam(name = "登录DTO", value = "传入带账号密码的json格式", readOnly = true)
                        @RequestBody LoginDTO driver) {
        log.debug(driver.getPhoneNum(), driver.getPassword());
        commonService.checkLoginCode(driver.getUuid(), driver.getCode());
        driver.setLoginRule(CommonConstants.DRIVER_ROLE);
        return commonService.login(driver);
    }

    @PostMapping("register")
    @ApiOperation("注册驾驶员账号")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return driverService.register(userRegisterDTO);
    }

    @PostMapping("certify")
    @ApiOperation("认证为驾驶员")
    public Result certify(@RequestBody UserRegisterDTO userRegisterDTO) {
        return driverService.register(userRegisterDTO);
    }

    @GetMapping("availableOrders")
    @ApiOperation("查询全部可抢订单")
    @PreAuthorize("hasRole('driver')")
    public Result availableOrders() {
        Map<Object, Object> map = orderService.availableOrders();
        return Result.ok().data(map);
    }

    @GetMapping("/pageAvailableOrder")
    @ApiOperation(value = "分页查可下单订单")
    public Result selectAll(Page<Order> page) {
        Page<Order> pageInfo = orderService.page(page,
                new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, OrderStatus.PUBLISHED));
        return Result.ok().data("pageInfo", pageInfo);
    }

    @RequestMapping("/push/{toUserId}")
    public ResponseEntity<String> pushToWeb(String message, @PathVariable String toUserId) throws IOException {
        WebSocketServer.sendInfo(message, toUserId);
        return ResponseEntity.ok("MSG SEND SUCCESS");
    }

}
