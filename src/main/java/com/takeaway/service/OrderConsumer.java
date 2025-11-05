package com.takeaway.service;

import com.takeaway.entity.Order;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "order-topic", consumerGroup = "takeaway-consumer-group")
public class OrderConsumer implements RocketMQListener<Order> {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);
    
    @Override
    public void onMessage(Order order) {
        logger.info("收到订单消息: 订单号={}, 用户ID={}, 总金额={}", 
                order.getOrderNo(), order.getUserId(), order.getTotalAmount());
        
        // 这里可以处理订单相关的业务逻辑
        // 例如：发送通知、更新库存、生成配送单等
    }
}


