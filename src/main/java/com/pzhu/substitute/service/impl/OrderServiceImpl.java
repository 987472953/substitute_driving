package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.substitute.common.*;
import com.pzhu.substitute.common.status.OrderStatus;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.entity.dto.OrderDTO;
import com.pzhu.substitute.mapper.OrderMapper;
import com.pzhu.substitute.mapper.UserMapper;
import com.pzhu.substitute.service.OrderService;
import com.pzhu.substitute.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    private UserMapper userMapper;

    @Override
    public Result getTradeNo(String userId) {
        String tradeKey = CommonConstants.USER_PREFIX + userId + CommonConstants.ORDER_SUFFIX;
        String tradeCode = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put("code", tradeCode);
        if (redisUtil.set(tradeKey, tradeCode, 60 * 20)) {
            return Result.ok(map);
        } else {
            return Result.error(ResultCode.REDIS_ERROR);
        }
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
    public Integer createTradeOrder(UserInfo userInfo, OrderDTO orderDTO) {
        //todo 看是否被锁定或拉黑
        if (userInfo == null) {
            throw new BizException(ResultCode.USER_DOES_NOT_EXIST);
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        double price = calculateThePrice(order.getDistance());
        order.setActualAmount(price);
        order.setPaymentAmount(0.0);
        order.setOrderStatus(OrderStatus.DRAFT);
        return orderMapper.insert(order);
    }

    /**
     * 计算价格
     * todo 后续添加优惠券
     * @param distance 距离
     * @return 价格
     */
    private double calculateThePrice(Integer distance) {
        return distance * 0.01;
    }
}

