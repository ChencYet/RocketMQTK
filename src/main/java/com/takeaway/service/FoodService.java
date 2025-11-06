package com.takeaway.service;

import com.takeaway.entity.Food;
import com.takeaway.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FoodService {
    
    @Autowired
    private FoodRepository foodRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String FOOD_LIST_CACHE = "food:list";
    private static final String FOOD_CACHE_PREFIX = "food:";
    
    public List<Food> getAllFoods() {
        // 先从Redis获取
        List<Food> foods = (List<Food>) redisTemplate.opsForValue().get(FOOD_LIST_CACHE);
        if (foods != null) {
            return foods;
        }
        // Redis没有则从数据库获取
        foods = foodRepository.findAll();
        // 存入Redis，设置10分钟过期
        redisTemplate.opsForValue().set(FOOD_LIST_CACHE, foods, 10, TimeUnit.MINUTES);
        return foods;
    }
    
    public Food getFoodById(Long id) {
        // 先从Redis获取
        Food food = (Food) redisTemplate.opsForValue().get(FOOD_CACHE_PREFIX + id);
        if (food != null) {
            return food;
        }
        // Redis没有则从数据库获取
        food = foodRepository.findById(id).orElse(null);
        if (food != null) {
            redisTemplate.opsForValue().set(FOOD_CACHE_PREFIX + id, 
                    food, 10, TimeUnit.MINUTES);
        }
        return food;
    }
    
    public List<Food> getFoodsByCategory(Long categoryId) {
        return foodRepository.findByCategoryId(categoryId);
    }
    
    public Food saveFood(Food food) {
        Food saved = foodRepository.save(food);
        // 清除缓存
        redisTemplate.delete(FOOD_LIST_CACHE);
        return saved;
    }
}



