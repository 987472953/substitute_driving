package com.pzhu.substitute.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.ResultCode;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.dto.OrderDTO;
import com.pzhu.substitute.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author dengyiqing
 * @description 订单表(Order)表控制层
 * @date 2022-02-10 18:03:33
 */
@RestController
@Api(tags = "订单表(Order)")
@RequestMapping("order")
public class OrderController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private OrderService orderService;


    @PostMapping("trade")
    @ApiOperation("草稿订单")
    public Result trace(@RequestBody OrderDTO orderDTO, Authentication authentication){
        LoginUser principal =(LoginUser) authentication.getPrincipal();
        String username = principal.getUsername();
        //计算总价格
        orderService.createTradeOrder(principal.getUserInfo(), orderDTO);
        /**
         * 流程 第一个接口将 user 为key uuid 为value的值存入redis 将uuid返回
         * 另一个接口提交订单时需要传入uuid, 保存订单之前需要判断这个uuid是否在redis中,不在则为过期
         */
        //防止重复提交
        return orderService.getTradeNo(username);
    }

    @PostMapping("submitOrder")
    @ApiOperation("提交订单")
    public Result submitOrder(String tradeNo, HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        boolean canSubmit = orderService.checkTradeNo(userId, tradeNo);
        if (canSubmit){
            //todo 改状态
//           return orderService.submitOrder(tradeNo);
           orderService.delTradeNo(userId);
        }else{
            return Result.error(ResultCode.ORDER_EXPIRED);
        }
        return Result.error(ResultCode.ORDER_EXPIRED);
    }

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param order 查询实体
     * @return 所有数据
     */
    @GetMapping("/selectAll")
    @ApiOperation(value = "分页查全部")
    public R selectAll(Page<Order> page, @ApiParam Order order) {
        return success(this.orderService.page(page, new QueryWrapper<>(order)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据id查")
    public R selectOne(@PathVariable("id") Integer id) {
        return success(this.orderService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param order 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加")
    public R insert(@RequestBody @ApiParam Order order) {
        return success(this.orderService.save(order));
    }

    /**
     * 修改数据
     *
     * @param order 实体对象
     * @return 修改结果
     */
    @PutMapping("/update")
    @ApiOperation(value = "更新")
    public R update(@RequestBody @ApiParam Order order) {
        return success(this.orderService.updateById(order));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping("/del")
    @ApiOperation(value = "删除")
    public R delete(@RequestParam("idList") @ApiParam List<Integer> idList) {
        return success(this.orderService.removeByIds(idList));
    }
}

