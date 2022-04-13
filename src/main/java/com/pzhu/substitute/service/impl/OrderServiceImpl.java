package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.ResultCode;
import com.pzhu.substitute.common.status.OrderStatus;
import com.pzhu.substitute.config.WebSocketServer;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.OrderLog;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.entity.dto.OrderDTO;
import com.pzhu.substitute.entity.dto.OrderPriceDTO;
import com.pzhu.substitute.mapper.OrderLogMapper;
import com.pzhu.substitute.mapper.OrderMapper;
import com.pzhu.substitute.mq.producer.Producer;
import com.pzhu.substitute.service.OrderService;
import com.pzhu.substitute.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author dengyiqing
 * @description 订单表(Order)表服务实现类
 * @date 2022-02-10 18:03:40
 */
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private Producer producer;

    @Value("${order.price.rate}")
    private Double rate;

    @Override
    public String getTradeNo(String userId) {
        String tradeKey = CommonConstants.USER_PREFIX + userId + CommonConstants.ORDER_SUFFIX;
        String tradeCode = UUID.randomUUID().toString();
        if (!redisUtil.set(tradeKey, tradeCode, 60 * 20)) {
            throw new BizException(ResultCode.REDIS_ERROR);
        }
        return tradeCode;
    }

    @Override
    public boolean checkTradeNo(String userId, String tradeNo) {
        String tradeKey = CommonConstants.USER_PREFIX + userId + CommonConstants.ORDER_SUFFIX;
        Object redisTradeNo = redisUtil.get(tradeKey);
        return tradeNo.equals(redisTradeNo);
    }

    @Override
    public void delTradeNo(String userId) {
        String tradeKey = CommonConstants.USER_PREFIX + userId + CommonConstants.ORDER_SUFFIX;
        redisUtil.del(tradeKey);
    }

    @Override
    @Transactional
    public Long createTradeOrder(UserInfo userInfo, OrderDTO orderDTO) {
        //todo 看是否被锁定或拉黑
        if (userInfo == null) {
            throw new BizException(ResultCode.USER_DOES_NOT_EXIST);
        }
        Order order = new Order();
        OrderLog orderLog = new OrderLog();
        BeanUtils.copyProperties(orderDTO, order);
        double price = calculateThePrice(order.getDistance());
        order.setActualAmount(price);
        order.setPaymentAmount(0.0);
        order.setOrderStatus(OrderStatus.DRAFT);
        order.setOrderPhone(userInfo.getPhoneNum());
        orderLog.setOrderId(order.getId());
        orderLog.setOrderStatus(OrderStatus.DRAFT);
        int orderInsert = orderMapper.insert(order);
        orderLogMapper.insert(orderLog);
        if (orderInsert == 1) {
            saveOrderToRedis(order);
        }
        return order.getId();
    }

    private void saveOrderToRedis(Order order) {
        for (int i = 0; i < 5; i++) {
            if (redisUtil.hset(CommonConstants.ORDER_LIST, order.getId().toString(), order)) {
                WebSocketServer.sendAllMessage(new Gson().toJson(ImmutableMap.of("insert", order)));
                break;
            } else if (i == 4) {
                producer.sendMessage(CommonConstants.MQ_ORDER_TOPIC, order, "insert");
            }
        }
    }


    @Override
    public List<Order> queryMyOrders(String username) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderPhone, username);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Double> calculateThePrice(UserInfo userInfo, OrderPriceDTO orderDTO) {
        HashMap<String, Double> map = new HashMap<>();
        // 计算总价格
        map.put(CommonConstants.PRICE_TO_PAY, calculateThePrice(orderDTO.getDistance()));
        // 计算距离费用
        map.put(CommonConstants.DISTANCE_PRICE, orderDTO.getDistance() * rate);
        // 计算优惠券费用
        map.put(CommonConstants.COUPON_DISCOUNT, 0.0);
        return map;
    }

    @Override
    public Map<Object, Object> availableOrders() {
        if (!redisUtil.hasKey(CommonConstants.ORDER_LIST)) {
            if(!redisUtil.lock(CommonConstants.REDIS_ORDER_LOCK, 10)){
                return availableOrders();
            }
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOrderStatus, OrderStatus.CREATED);
            List<Order> orders = orderMapper.selectList(wrapper);
            HashMap<String, Object> map = new HashMap<>();
            for (Order order : orders) {
                map.put(order.getId().toString(), order);
            }
            redisUtil.hmset(CommonConstants.ORDER_LIST, map);
        }
        return redisUtil.hmget(CommonConstants.ORDER_LIST);
    }

    /**
     * 计算价格
     * todo 后续添加优惠券
     *
     * @param distance 距离
     * @return 价格
     */
    private double calculateThePrice(Integer distance) {
        return distance * rate;
    }
}

