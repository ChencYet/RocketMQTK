package com.takeaway.service;

import com.takeaway.entity.Message;
import com.takeaway.repository.MessageRepository;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "message-topic", consumerGroup = "takeaway-consumer-group")
public class MessageConsumer implements RocketMQListener<Message> {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void onMessage(Message message) {
        logger.info("收到RocketMQ消息: id={}, from={}, to={}, content={}",
                message.getId(), message.getFromUserId(), message.getToUserId(), message.getContent());

        // 消息已在发送时保存到数据库，这里只做通知相关处理
        // 如果消息已存在（有ID），则跳过保存，避免重复
        if (message.getId() != null && messageRepository.existsById(message.getId())) {
            logger.debug("消息已存在，跳过保存: id={}", message.getId());
        } else {
            // 如果消息不存在，则保存（防止异常情况）
            messageRepository.save(message);
            logger.info("消息不存在，已保存: id={}", message.getId());
        }
        
        // 推送给接收用户（WebSocket实时推送）
        try {
            webSocketService.pushMessageToUser(message.getToUserId(), message);
            logger.info("WebSocket推送成功: userId={}, messageId={}", 
                       message.getToUserId(), message.getId());
        } catch (Exception e) {
            logger.error("WebSocket推送失败: userId={}, messageId={}, error={}", 
                        message.getToUserId(), message.getId(), e.getMessage(), e);
            // 推送失败不影响消息已保存的状态，可以后续重试
        }
        
        // 这里可以添加其他异步处理逻辑，比如：
        // - 更新未读消息数
        // - 发送邮件/短信通知
        // - 触发其他业务逻辑等
    }
}


