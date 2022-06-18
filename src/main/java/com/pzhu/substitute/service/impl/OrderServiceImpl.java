package com.pzhu.substitute.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.common.status.OrderStatus;
import com.pzhu.substitute.config.WebSocketServer;
import com.pzhu.substitute.entity.*;
import com.pzhu.substitute.entity.dto.OrderDTO;
import com.pzhu.substitute.entity.dto.OrderPriceDTO;
import com.pzhu.substitute.entity.dto.PaymentDTO;
import com.pzhu.substitute.mapper.MessageMapper;
import com.pzhu.substitute.mapper.OrderLogMapper;
import com.pzhu.substitute.mapper.OrderMapper;
import com.pzhu.substitute.mapper.OrderRefundMapper;
import com.pzhu.substitute.mq.producer.Producer;
import com.pzhu.substitute.service.OrderService;
import com.pzhu.substitute.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author dengyiqing
 * @description 订单表(Order)表服务实现类
 * @date 2022-02-10 18:03:40
 */
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final RedisUtil redisUtil;
    private final OrderMapper orderMapper;
    private final OrderLogMapper orderLogMapper;
    private final MessageMapper messageMapper;
    private final Producer producer;
    private final AlipayClient certAlipayClient;
    private final OrderRefundMapper orderRefundMapper;

    @Autowired
    public OrderServiceImpl(RedisUtil redisUtil, OrderMapper orderMapper, OrderLogMapper orderLogMapper, MessageMapper messageMapper, Producer producer, AlipayClient certAlipayClient, OrderRefundMapper orderRefundMapper) {
        this.redisUtil = redisUtil;
        this.orderMapper = orderMapper;
        this.orderLogMapper = orderLogMapper;
        this.messageMapper = messageMapper;
        this.producer = producer;
        this.certAlipayClient = certAlipayClient;
        this.orderRefundMapper = orderRefundMapper;
    }

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
        BeanUtils.copyProperties(orderDTO, order);
        double price = calculateThePrice(order.getDistance());
        order.setUserId(userInfo.getId());
        order.setActualAmount(0.0);
        order.setPaymentAmount(price);
        order.setOrderStatus(OrderStatus.DRAFT);
        order.setOrderPhone(userInfo.getPhoneNum());
        if (!orderDTO.getIsReservation()) {
            order.setAppointmentTime(null);
        }
        int orderInsert = orderMapper.insert(order);
        if (orderInsert == 1) {
            OrderLog orderLog = new OrderLog(null, order.getId(), OrderStatus.DRAFT, null);
            orderLogMapper.insert(orderLog);
            saveOrderToRedis(order);
        }
        return order.getId();
    }

    private void saveOrderToRedis(Order order) {
        for (int i = 0; i < 5; i++) {
            if (redisUtil.zSet(CommonConstants.ORDER_LIST, order, order.getId().doubleValue())) {
                WebSocketServer.sendAllMessage(new Gson().toJson(ImmutableMap.of("order", order, "type", "nowOrder")));
                break;
            } else if (i == 4) {
                producer.sendMessage(CommonConstants.MQ_ORDER_TOPIC, order, "insert");
            }
        }
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
        Map<Object, Object> map = new HashMap<>();
        if (!redisUtil.hasKey(CommonConstants.ORDER_LIST)) {
            if (!redisUtil.lock(CommonConstants.REDIS_ORDER_LOCK, 10)) {
                return availableOrders();
            }
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOrderStatus, OrderStatus.CREATED);
            List<Order> orders = orderMapper.selectList(wrapper);
            for (Order order : orders) {
                redisUtil.zSet(CommonConstants.ORDER_LIST, order, order.getId().doubleValue());
            }
            map.put("orderList", orders);
        } else {
            Set<Object> objects = redisUtil.zGetAll(CommonConstants.ORDER_LIST);
            map.put("orderList", objects);
        }
        return map;
    }

    @Override
    public Boolean takeOrder(Long orderId, DriverInfo driverInfo) {
        Order order = orderMapper.selectById(orderId);
        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("订单状态错误");
        }
        Order updateOrder = new Order();
        updateOrder.setId(orderId);
        updateOrder.setDriverId(driverInfo.getId());
        updateOrder.setOrderStatus(OrderStatus.ASSIGNED);
        order.setDriverId(driverInfo.getId());
        order.setOrderStatus(OrderStatus.ASSIGNED);
        int i = orderMapper.updateById(updateOrder);
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setOrderStatus(OrderStatus.ASSIGNED);
        orderLogMapper.insert(orderLog);
        String redisKey = CommonConstants.DRIVER_PREFIX + driverInfo.getId() + CommonConstants.ORDER_PREFIX;
        redisUtil.set(redisKey, order);
        return true;
    }

    @Override
    public List<Message> queryMessageList(Long orderId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getOrderId, orderId).orderByAsc(Message::getCreateTime);
        List<Message> messageList = messageMapper.selectList(wrapper);
        return messageList;
    }

    @Override
    public void completeOrderPayment(PaymentDTO paymentDTO) {
        Order order = new Order();
        order.setId(paymentDTO.getOut_trade_no());
        order.setTradeNo(paymentDTO.getTrade_no());
        order.setActualAmount(paymentDTO.getTotal_amount());
        order.setOrderStatus(OrderStatus.CREATED);
        int i = orderMapper.updateById(order);
        if (i == 1) {
            OrderLog orderLog = new OrderLog(null, order.getId(), OrderStatus.CREATED, null);
            orderLogMapper.insert(orderLog);
        }
    }

    @Override
    public Double refundOrder(Long orderId, UserInfo userInfo) throws AlipayApiException {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userInfo.getId()).eq(Order::getId, orderId);
        Order order = orderMapper.selectOne(wrapper);
        if (Objects.isNull(order)) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("订单不存在");
        }
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        request.setBizModel(model);

        OrderRefund orderRefund = new OrderRefund(null, order.getId(), order.getTradeNo(), order.getActualAmount(), null);
        orderRefundMapper.insert(orderRefund);

        model.setRefundAmount(order.getActualAmount().toString());
        model.setOutTradeNo(order.getId().toString());
        model.setTradeNo(order.getTradeNo());
        model.setOutRequestNo(orderRefund.getId().toString());
        AlipayTradeRefundResponse response = certAlipayClient.certificateExecute(request);//通过alipayClient调用API，获得对应的response类
        System.out.print(response.getBody());
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject refundResponse = jsonObject.get("alipay_trade_refund_response").getAsJsonObject();
        BigDecimal refundFee = refundResponse.get("refund_fee").getAsBigDecimal();

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setOrderStatus(OrderStatus.CANCELLED);
        updateOrder.setActualAmount(0.0);
        orderMapper.updateById(updateOrder);

        return refundFee.doubleValue();
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

