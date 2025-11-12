package com.takeaway.controller;

import com.takeaway.entity.Food;
import com.takeaway.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "食品管理")
@RestController
@RequestMapping("/takeaway/food")
public class FoodController {
    
    @Autowired
    private FoodService foodService;
    
    @GetMapping("/list")
    @Operation(summary = "获取所有食品列表")
    public ResponseEntity<Map<String, Object>> getAllFoods() {
        Map<String, Object> result = new HashMap<>();
        List<Food> foods = foodService.getAllFoods();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foods);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取食品详情")
    public ResponseEntity<Map<String, Object>> getFoodById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Food food = foodService.getFoodById(id);
        if (food != null) {
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", food);
        } else {
            result.put("code", 404);
            result.put("message", "商品不存在");
        }
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取食品列表")
    public ResponseEntity<Map<String, Object>> getFoodsByCategory(@PathVariable Long categoryId) {
        Map<String, Object> result = new HashMap<>();
        List<Food> foods = foodService.getFoodsByCategory(categoryId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foods);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save")
    @Operation(summary = "保存食品信息")
    public ResponseEntity<Map<String, Object>> saveFood(@RequestBody Food food) {
        Map<String, Object> result = new HashMap<>();
        Food saved = foodService.saveFood(food);
        result.put("code", 200);
        result.put("message", "保存成功");
        result.put("data", saved);
        return ResponseEntity.ok(result);
    }
}



