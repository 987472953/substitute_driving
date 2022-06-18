package com.pzhu.substitute.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pzhu.substitute.entity.*;
import com.pzhu.substitute.entity.dto.OrderDTO;
import com.pzhu.substitute.entity.dto.OrderPriceDTO;
import com.pzhu.substitute.entity.dto.PaymentDTO;

import java.util.List;
import java.util.Map;

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
    String getTradeNo(String userId);

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
    Long createTradeOrder(UserInfo userInfo, OrderDTO orderDTO);



    /**
     * 计算价格
     * @param userInfo
     * @param orderDTO
     * @return
     */
    Map<String, Double> calculateThePrice(UserInfo userInfo, OrderPriceDTO orderDTO);

    Map<Object, Object> availableOrders();

    /**
     * 驾驶员接收订单
     * @param orderId
     * @param driverInfo
     * @return
     */
    Boolean takeOrder(Long orderId, DriverInfo driverInfo);

    /**
     * 查询订单的交流
     * @param orderId
     * @return
     */
    List<Message> queryMessageList(Long orderId);

    /**
     * 完成订单支付
     * @param paymentDTO
     */
    void completeOrderPayment(PaymentDTO paymentDTO);

    /**
     * 订单退款
     * @param orderId
     * @param userInfo
     * @return
     */
    Double refundOrder(Long orderId, UserInfo userInfo) throws AlipayApiException;
}
