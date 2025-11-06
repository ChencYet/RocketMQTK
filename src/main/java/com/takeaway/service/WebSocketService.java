package com.takeaway.service;

import com.takeaway.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 推送消息给指定用户
     * @param userId 接收用户ID
     * @param message 消息对象
     */
    public void pushMessageToUser(Long userId, Message message) {
        try {
            // 构建推送的数据
            MessagePushData pushData = new MessagePushData();
            pushData.setType("NEW_MESSAGE");
            pushData.setMessage(message);
            
            // 发送到用户专属的队列：/queue/message/{userId}
            String destination = "/queue/message/" + userId;
            messagingTemplate.convertAndSend(destination, pushData);
            
            logger.info("推送消息成功: userId={}, messageId={}", userId, message.getId());
        } catch (Exception e) {
            logger.error("推送消息失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 推送消息给所有在线用户（广播）
     */
    public void broadcastMessage(Message message) {
        try {
            MessagePushData pushData = new MessagePushData();
            pushData.setType("NEW_MESSAGE");
            pushData.setMessage(message);
            
            messagingTemplate.convertAndSend("/topic/messages", pushData);
            logger.info("广播消息成功: messageId={}", message.getId());
        } catch (Exception e) {
            logger.error("广播消息失败: error={}", e.getMessage(), e);
        }
    }

    /**
     * 消息推送数据封装类
     */
    public static class MessagePushData {
        private String type;
        private Message message;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }
}

