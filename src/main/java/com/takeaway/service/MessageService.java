package com.takeaway.service;

import com.takeaway.entity.Message;
import com.takeaway.repository.MessageRepository;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private static final String MESSAGE_TOPIC = "message-topic";

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private MessageRepository messageRepository;

    public Message send(Message message) {
        // 先保存到数据库，保证发送后立即可查
        Message savedMessage = messageRepository.save(message);
        
        // 发送到 RocketMQ，由Consumer异步处理推送（解耦处理）
        try {
            rocketMQTemplate.convertAndSend(MESSAGE_TOPIC, savedMessage);
            org.slf4j.LoggerFactory.getLogger(MessageService.class)
                    .debug("消息已发送到RocketMQ: messageId={}, toUserId={}", 
                           savedMessage.getId(), savedMessage.getToUserId());
        } catch (Exception e) {
            // 如果 MQ 发送失败，记录日志但不影响主流程（消息已保存到数据库）
            org.slf4j.LoggerFactory.getLogger(MessageService.class)
                    .warn("消息发送到 RocketMQ 失败，但已保存到数据库: {}", e.getMessage());
        }
        
        return savedMessage;
    }

    public List<Message> getInbox(Long toUserId) {
        return messageRepository.findByToUserIdOrderByCreateTimeDesc(toUserId);
    }

    public List<Message> getOutbox(Long fromUserId) {
        return messageRepository.findByFromUserIdOrderByCreateTimeDesc(fromUserId);
    }

    public List<Message> getConversation(Long userA, Long userB) {
        return messageRepository.findByFromUserIdAndToUserIdOrFromUserIdAndToUserIdOrderByCreateTimeDesc(
                userA, userB, userB, userA);
    }
}


