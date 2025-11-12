package com.takeaway.service;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
@Component
@RocketMQMessageListener(topic = "test-topic", consumerGroup = "test-consumer-group")
public class MessageTestConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("刚刚接到通知: " + message);
    }
}
