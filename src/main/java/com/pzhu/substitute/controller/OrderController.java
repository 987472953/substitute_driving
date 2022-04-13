package com.pzhu.substitute.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.ResultCode;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.dto.OrderDTO;
import com.pzhu.substitute.entity.dto.OrderPriceDTO;
import com.pzhu.substitute.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author dengyiqing
 * @description 订单表(Order)表控制层
 * @date 2022-02-10 18:03:33
 */
@RestController
@Api(tags = "订单表(Order)")
@RequestMapping("order")
@Slf4j
public class OrderController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private OrderService orderService;

    @PostMapping("calPrice")
    @ApiOperation("预估价格")
    public Result calPrice(@RequestBody OrderPriceDTO orderPriceDTO, Authentication authentication) {
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String username = principal.getUsername();

        Map<String, Double> map = orderService.calculateThePrice(principal.getUserInfo(), orderPriceDTO);
        //防止重复提交
        String tradeNo = orderService.getTradeNo(username);
        ImmutableMap<Object, Object> of = ImmutableMap.of("priceInfo", map, "tradeNo", tradeNo);
        return Result.ok().data(of);
    }

    @PostMapping("submitOrder")
    @ApiOperation("提交订单")
    public Result submitOrder(@RequestBody OrderDTO orderDTO, String tradeNo, Authentication authentication) {
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String username = principal.getUsername();
        boolean canSubmit = orderService.checkTradeNo(username, tradeNo);
        if (!canSubmit) {
            return Result.error(ResultCode.ORDER_EXPIRED);
        }
        Long orderId = orderService.createTradeOrder(principal.getUserInfo(), orderDTO);
        orderService.delTradeNo(username);
        return Result.ok().data("orderId", orderId);
    }

    @GetMapping("iOrders")
    @ApiOperation("查询我的全部订单")
    public Result iOrders(Authentication authentication) {
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String username = principal.getUsername();
        List<Order> orders = orderService.queryMyOrders(username);
        return Result.ok().data("orders", orders);
    }

    /**
     * 分页查询所有数据
     *
     * @param page  分页对象
     * @param order 查询实体
     * @return 所有数据
     */
    @GetMapping("/selectAll")
    @ApiOperation(value = "分页查全部")
    public Result selectAll(Page<Order> page, @ApiParam Order order) {
        Page<Order> pageInfo = orderService.page(page, new QueryWrapper<>(order));
        return Result.ok().data("pageInfo", pageInfo);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据id查")
    public Result selectOne(@PathVariable("id") Integer id) {
        Order byId = orderService.getById(id);
        return Result.ok().data("order", byId);
    }
}

