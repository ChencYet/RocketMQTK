package com.takeaway.controller;

import com.takeaway.entity.Food;
import com.takeaway.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/food")
public class FoodController {
    
    @Autowired
    private FoodService foodService;
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllFoods() {
        Map<String, Object> result = new HashMap<>();
        List<Food> foods = foodService.getAllFoods();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foods);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
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
    public ResponseEntity<Map<String, Object>> getFoodsByCategory(@PathVariable Long categoryId) {
        Map<String, Object> result = new HashMap<>();
        List<Food> foods = foodService.getFoodsByCategory(categoryId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foods);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveFood(@RequestBody Food food) {
        Map<String, Object> result = new HashMap<>();
        Food saved = foodService.saveFood(food);
        result.put("code", 200);
        result.put("message", "保存成功");
        result.put("data", saved);
        return ResponseEntity.ok(result);
    }
}



