package com.takeaway.controller;

import com.takeaway.entity.Order;
import com.takeaway.entity.OrderItem;
import com.takeaway.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/takeaway/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) params.get("items");
            
            // 将Map转换为OrderItem对象
            List<OrderItem> items = new java.util.ArrayList<>();
            for (Map<String, Object> itemMap : itemsMap) {
                OrderItem item = new OrderItem();
                item.setFoodId(Long.valueOf(itemMap.get("foodId").toString()));
                item.setQuantity(Integer.valueOf(itemMap.get("quantity").toString()));
                items.add(item);
            }
            
            String address = params.get("address").toString();
            String phone = params.get("phone").toString();
            
            Order order = orderService.createOrder(userId, items, address, phone);
            result.put("code", 200);
            result.put("message", "订单创建成功");
            result.put("data", order);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID获取订单列表")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Order> orders = orderService.getOrdersByUserId(userId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", orders);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{orderNo}")
    @Operation(summary = "根据订单号获取订单详情")
    public ResponseEntity<Map<String, Object>> getOrderByOrderNo(@PathVariable String orderNo) {
        Map<String, Object> result = new HashMap<>();
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order != null) {
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", order);
        } else {
            result.put("code", 404);
            result.put("message", "订单不存在");
        }
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{orderId}/status")
    @Operation(summary = "更新订单状态")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String status = params.get("status");
            Order order = orderService.updateOrderStatus(orderId, status);
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", order);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}

