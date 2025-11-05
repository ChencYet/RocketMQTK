package com.takeaway.service;

import com.takeaway.entity.User;
import com.takeaway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_CACHE_PREFIX = "user:";
    
    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        return userRepository.save(user);
    }
    
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            // 将用户信息存入Redis，设置30分钟过期
            redisTemplate.opsForValue().set(USER_CACHE_PREFIX + user.getId(), 
                    user, 30, TimeUnit.MINUTES);
            return user;
        }
        throw new RuntimeException("用户名或密码错误");
    }
    
    public User getUserById(Long id) {
        // 先从Redis获取
        User user = (User) redisTemplate.opsForValue().get(USER_CACHE_PREFIX + id);
        if (user != null) {
            return user;
        }
        // Redis没有则从数据库获取
        user = userRepository.findById(id).orElse(null);
        if (user != null) {
            redisTemplate.opsForValue().set(USER_CACHE_PREFIX + id, 
                    user, 30, TimeUnit.MINUTES);
        }
        return user;
    }
}


