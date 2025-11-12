package com.takeaway.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket控制器示例
 * 客户端可以通过这个控制器发送消息到服务器
 */
@Controller
public class WebSocketController {

    /**
     * 客户端发送消息到 /app/chat 时，会触发这个方法
     * 消息会广播到 /topic/messages
     */
    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
    @SendToUser("/queue/message")
    public Map<String, Object> handleChatMessage(Map<String, Object> message) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "CHAT_RESPONSE");
        response.put("content", "收到消息: " + message.get("content"));
        return response;
    }
}

