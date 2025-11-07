package com.takeaway.controller;

import com.takeaway.entity.User;
import com.takeaway.service.UserService;
import com.takeaway.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "用户接口", description = "用户注册、登录及信息获取接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User savedUser = userService.register(user);
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("data", savedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT token")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = params.get("username");
            String password = params.get("password");
            User user = userService.login(username, password);
            // 生成jwt
            String token = JwtUtil.generateToken(username);
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", user);
            result.put("token", token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getUserById(id);
        if (user != null) {
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        return ResponseEntity.ok(result);
    }
}



