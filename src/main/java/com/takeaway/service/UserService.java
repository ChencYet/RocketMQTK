package com.takeaway.service;

import com.takeaway.dto.UserLoginDto;
import com.takeaway.entity.User;
import com.takeaway.repository.UserRepository;
import com.takeaway.request.UserPageRequest;
import com.takeaway.response.UserPageResponse;
import com.takeaway.util.JwtUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:";

    private static final String TOKEN_PREFIX = "token:";

    @Autowired
    private JwtUtil jwtUtil;

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        return userRepository.save(user);
    }
    
    public UserLoginDto login(String username, String password) {
        UserLoginDto userLoginDto = new UserLoginDto();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            // 生成token
            String token = jwtUtil.generateToken(username);
            userLoginDto.setToken(token);
            userLoginDto.setUser(user);
            // 把token写redis缓存，设置1天过期
            redisTemplate.opsForValue().set(TOKEN_PREFIX + token, user.getId(), 1, TimeUnit.DAYS);
            logger.info("存储token到Redis: {} -> 用户ID: {} (类型: {})", 
                TOKEN_PREFIX + token, user.getId(), user.getId().getClass().getSimpleName());

            // 将用户信息存入Redis，设置30分钟过期
            redisTemplate.opsForValue().set(USER_CACHE_PREFIX + user.getId(), 
                    user, 30, TimeUnit.MINUTES);



            return userLoginDto;
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

    @Transactional
    public User updateUserPassword(User user) {
        //  1. 先查出来
        User userFromDb = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("用户不存在"));
        // 2. 更新密码
        userFromDb.setUsername(user.getUsername());
        userFromDb.setAddress(user.getAddress());
        userFromDb.setPassword(user.getPassword());

        //
        userFromDb.setUpdateTime(new Date());
        // 3. 保存到数据库
        userRepository.save(userFromDb);
        // 更新Redis缓存
        redisTemplate.opsForValue().set(USER_CACHE_PREFIX + user.getId(),
                userFromDb, 30, TimeUnit.MINUTES);
        return userFromDb;
    }

    public Long getUserIdByToken(String token) {
        Object userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        logger.info("从Redis获取用户ID: {} -> 结果: {} (类型: {})", 
            TOKEN_PREFIX + token, userId, userId != null ? userId.getClass().getSimpleName() : "null");
        
        if (userId != null) {
            if (userId instanceof Number) {
                Long result = ((Number) userId).longValue();
                logger.info("转换后的用户ID: {}", result);
                return result;
            } else {
                // 处理可能的类型转换问题
                try {
                    Long result = Long.valueOf(userId.toString());
                    logger.info("字符串转换后的用户ID: {}", result);
                    return result;
                } catch (NumberFormatException e) {
                    logger.error("无法将Redis中的值转换为Long: {}", userId, e);
                    return null;
                }
            }
        }
        logger.info("Redis中未找到token: {}", TOKEN_PREFIX + token);
        return null;
    }

    // 退出登录
    public void logout(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }

    // 分页获取用户列表
    public UserPageResponse<User> getUsersByPage(UserPageRequest pageRequest) {
        // 1. 构建分页请求
        Pageable pageable = PageRequest.of(
            pageRequest.getPageNum() - 1, // PageRequest从0开始
            pageRequest.getPageSize(),
            Sort.by(Sort.Direction.fromString(pageRequest.getIsAsc()), pageRequest.getSortBy())
        );
        // 2. 查询分页数据
        Specification<User> spec = (root, query, critetiaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 用户id
            if (pageRequest.getUserId() != null) {
                predicates.add(critetiaBuilder.equal(root.get("id"), pageRequest.getUserId()));
            }
            // 用户名模糊查询
            if (pageRequest.getUsername() != null) {
                predicates.add(critetiaBuilder.like(root.get("username"), "%" + pageRequest.getUsername() + "%"));
            }
            // 手机号模糊查询
            if (pageRequest.getPhone() != null) {
                predicates.add(critetiaBuilder.like(root.get("phone"), "%" + pageRequest.getPhone() + "%"));
            }
            // 邮箱模糊查询
            if (pageRequest.getEmail() != null) {
                predicates.add(critetiaBuilder.like(root.get("email"), "%" + pageRequest.getEmail() + "%"));
            }
            // 地址模糊查询
            if (pageRequest.getAddress() != null) {
                predicates.add(critetiaBuilder.like(root.get("address"), "%" + pageRequest.getAddress() + "%"));
            }

            // 创建时间范围
            if (pageRequest.getStartTime() != null) {
                predicates.add(critetiaBuilder.greaterThanOrEqualTo(root.get("createTime"), pageRequest.getStartTime()));
            }
            if (pageRequest.getEndTime() != null) {
                predicates.add(critetiaBuilder.lessThanOrEqualTo(root.get("createTime"), pageRequest.getEndTime()));
            }

            return critetiaBuilder.and(predicates.toArray(new Predicate[0]));
        };


        Page<User> userPage = userRepository.findAll(spec, pageable);

        // 3. 构建响应对象
        return UserPageResponse.success(userPage.getContent());
    }
}