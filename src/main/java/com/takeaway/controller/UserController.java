package com.takeaway.controller;

import com.takeaway.Exception.CustomException;
import com.takeaway.dto.UserLoginDto;
import com.takeaway.entity.User;
import com.takeaway.request.UserPageRequest;
import com.takeaway.response.UserPageResponse;
import com.takeaway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/takeaway/user")
@Tag(name = "用户接口", description = "用户注册、登录及信息获取接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
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
            UserLoginDto userLoginDto = userService.login(username, password);
            // 生成jwt
            // String token = JwtUtil.generateToken(username);
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", userLoginDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户详情", description = "根据用户ID获取用户详细信息")
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

    @PostMapping("/update")
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User updatedUser = userService.updateUserPassword(user);
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

//    @PostMapping("/page")
//    @Operation(summary = "分页查询用户", description = "根据分页参数查询用户列表")
//    public ResponseEntity<Map<String, Object>> getUsersByPage(@RequestBody UserPageRequest pageRequest) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            Page<User> users = userService.getUsersByPage(pageRequest);
//            result.put("code", 200);
//            result.put("message", "查询成功");
//            result.put("data", users);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            result.put("code", 500);
//            result.put("message", e.getMessage());
//            return ResponseEntity.ok(result);
//        }
//    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户", description = "根据分页参数查询用户列表")
    public ResponseEntity<UserPageResponse<User>> getUsersByPage(@RequestBody UserPageRequest pageRequest) {
        try {
            if (0 < 1) {
                throw new CustomException("模拟异常测试");
            }
            UserPageResponse<User> users = userService.getUsersByPage(pageRequest);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.ok(UserPageResponse.error());
        }
    }

}



