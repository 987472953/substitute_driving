package com.pzhu.substitute.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.Message;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.dto.OrderDTO;
import com.pzhu.substitute.entity.dto.OrderPriceDTO;
import com.pzhu.substitute.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("calPrice")
    @ApiOperation("订单预估价格")
    @PreAuthorize("hasRole('user')")
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


    @GetMapping("/selectAll")
    @ApiOperation(value = "分页查全部订单")
    public Result selectAll(Page<Order> page, @ApiParam Order order) {
        Page<Order> pageInfo = orderService.page(page, new QueryWrapper<>(order));
        return Result.ok().data("pageInfo", pageInfo);
    }

    @GetMapping("{id}")
    @ApiOperation(value = "根据id查订单信息")
    public Result selectOne(@PathVariable("id") Long id) {
        Order byId = orderService.getById(id);
        return Result.ok().data("order", byId);
    }

    @PostMapping("/message/{orderId}")
    @ApiOperation(value = "根据订单ID查询消息")
    public Result getOrderMessage(@PathVariable("orderId") Long orderId) {
        List<Message> messageList = orderService.queryMessageList(orderId);
        return Result.ok().data("messageList", messageList);
    }
}

