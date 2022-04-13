package com.pzhu.substitute.mq.producer;


import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

/**
 * @author dengyiqing
 * @description 生产消息抽象类
 * @date 2022/4/3
 */
public abstract class ProducerMessageAbstract<T> {

    /**
     * 创建消息
     *
     * @param t   消息实体类
     * @param map messageHeaders map
     * @return 消息
     */
    public Message<T> createMessage(T t, Map<String, Object> map) {
        return new Message<T>() {
            @Override
            public T getPayload() {
                return t;
            }

            @Override
            public MessageHeaders getHeaders() {
                return new MessageHeaders(map);
            }
        };
    }

    /**
     * 设置消息目标
     *
     * @param topic 消息主题
     * @param tags  消息标签
     * @return 消息目的地
     */
    public String messageDestination(String topic, String tags) {
        return topic +
                ":" +
                tags;
    }
}