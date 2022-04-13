package com.pzhu.substitute.mq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dengyiqing
 * @description RocketMQ 生产者
 * @date 2022/3/30
 */
@Component
@Slf4j
public class Producer extends ProducerMessageAbstract<Object> {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送普通消息
     *
     * @param topic  主题
     * @param object 消息
     */
    public void sendMessage(String topic, Object object, String tag) {
        log.debug("[消息队列]普通发送消息, topic: {}, message: {}", topic, object);
        Message<Object> message = createMessage(object, null);
        String destination = messageDestination(topic, tag);
        this.rocketMQTemplate.send(destination, message);
    }

    /**
     * 发送普通消息
     *
     * @param topic  主题
     * @param object 消息
     */
    public void sendMessageDes(String topic, Object object, String tag) {
        log.debug("[消息队列]普通发送消息, topic: {}, message: {}", topic, object);
        this.rocketMQTemplate.convertAndSend(topic + ":" + tag, object);
    }

    /**
     * 发送事务消息
     *
     * @param topic   主题
     * @param message 消息
     */
    public void sendMessageInTransaction(String topic, String message) throws InterruptedException {
        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            Message<String> msg = MessageBuilder.withPayload(message).build();
            String destination = topic + ":" + tags[i % tags.length];
            SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, msg, destination);
            System.out.printf("%s%n", sendResult);
            Thread.sleep(10);
        }
    }
}