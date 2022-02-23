package com.pzhu.substitute.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.entity.dto.OrderDTO;

/**
 * @author dengyiqing
 * @description 订单表(Order)表服务接口
 * @date 2022-02-10 18:03:40
 */
public interface OrderService extends IService<Order> {

    /**
     * 获得唯一订单标识
     * @param userId 用户id
     * @return uuid
     */
    Result getTradeNo(String userId);

    /**
     * 检查订单是否重复
     * @param userId 用户id
     * @param tradeNo 交易号
     * @return 是否重复
     */
    boolean checkTradeNo(String userId, String tradeNo);

    /**
     * 删除交易编号
     * @param userId 用户id
     */
    void delTradeNo(String userId);

    /**
     * 创建订单
     * @param userInfo 用户信息
     * @param orderDTO 订单信息
     * @return 插入条数
     */
    Integer createTradeOrder(UserInfo userInfo, OrderDTO orderDTO);
}
