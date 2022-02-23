package com.pzhu.substitute.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengyiqing
 * @description 驾驶员管理
 * @date 2022/2/8
 */
@RestController
@RequestMapping("user")
@Api(value = "驾驶员模块", tags = {"驾驶员操作接口"})
@Slf4j
public class DriverController {

}
