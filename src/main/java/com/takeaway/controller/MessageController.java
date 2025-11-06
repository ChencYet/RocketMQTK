package com.takeaway.controller;

import com.takeaway.entity.Message;
import com.takeaway.service.MessageService;
import com.takeaway.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private WebSocketService webSocketService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> send(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long fromUserId = Long.valueOf(body.get("fromUserId").toString());
            Long toUserId = Long.valueOf(body.get("toUserId").toString());
            String content = body.get("content").toString();

            Message message = new Message();
            message.setFromUserId(fromUserId);
            message.setToUserId(toUserId);
            message.setContent(content);

            Message savedMessage = messageService.send(message);

            result.put("code", 200);
            result.put("message", "发送成功");
            result.put("data", savedMessage);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/inbox/{userId}")
    public ResponseEntity<Map<String, Object>> inbox(@PathVariable("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Message> messages = messageService.getInbox(userId);
        result.put("code", 200);
        result.put("message", "OK");
        result.put("data", messages);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/outbox/{userId}")
    public ResponseEntity<Map<String, Object>> outbox(@PathVariable("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Message> messages = messageService.getOutbox(userId);
        result.put("code", 200);
        result.put("message", "OK");
        result.put("data", messages);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/conversation")
    public ResponseEntity<Map<String, Object>> conversation(@RequestParam("userA") Long userA,
                                                            @RequestParam("userB") Long userB) {
        Map<String, Object> result = new HashMap<>();
        List<Message> messages = messageService.getConversation(userA, userB);
        result.put("code", 200);
        result.put("message", "OK");
        result.put("data", messages);
        return ResponseEntity.ok(result);
    }

    /**
     * 测试WebSocket推送接口（用于调试）
     */
    @PostMapping("/test-push")
    public ResponseEntity<Map<String, Object>> testPush(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long toUserId = Long.valueOf(body.get("toUserId").toString());
            String content = body.get("content").toString();

            // 创建测试消息
            Message testMessage = new Message();
            testMessage.setFromUserId(0L); // 系统消息
            testMessage.setToUserId(toUserId);
            testMessage.setContent(content);

            // 直接推送（不保存到数据库）
            webSocketService.pushMessageToUser(toUserId, testMessage);

            result.put("code", 200);
            result.put("message", "测试推送成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}


