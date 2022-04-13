package com.pzhu.substitute.mq.consumer;

import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dengyiqing
 * @description 订单处理的消费者
 * @date 2022/3/30
 */
@Component
@RocketMQMessageListener(consumerGroup = "DrivingConsumerGroup", topic = CommonConstants.MQ_ORDER_TOPIC)
@Slf4j
public class Consumer implements RocketMQListener<Order> {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onMessage(Order message) {
        // 更新订单到redis缓存
        log.debug("[消息队列]消费消息, message: {}", message);
//        for (int i = 0; i < 5; i++) {
//            if (redisUtil.hset(CommonConstants.ORDER_LIST, message.getId().toString(), message)) {
//                WebSocketServer.sendAllMessage(new Gson().toJson(ImmutableMap.of("insert", message)));
//                break;
//            } else if (i == 4) {
//                throw new MQException(ResultCode.MQ_FAILED_CONSUME_MESSAGE);
//            }
//        }
//        throw new BizException(ResultCode.MQ_FAILED_CONSUME_MESSAGE);
    }
}