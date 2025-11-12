package com.takeaway.job;

import com.takeaway.entity.User;
import com.takeaway.repository.UserRepository;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
@Component
public class UserScheduleJob {

    private static final Logger log = LoggerFactory.getLogger(UserScheduleJob.class);

    private static final String LAST_MAX_TIME_KEY = "user:updated:maxtime";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 每十秒执行一次
//    @Scheduled(cron = "*/10 * * * * ?")
    @Scheduled(fixedRate = 10_000)
    public void syncUserScore() {
        log.info("同步用户积分任务执行中...");
        // 这里可以添加具体的同步逻辑
        // TODO
        // 如果用户被更新，提示用户
        // 1.取出上次扫描最大时间
        String lastMax = (String) redisTemplate.opsForValue().get(LAST_MAX_TIME_KEY);
        Date start;
        try {
            start = lastMax == null
                    ? new Date()
                    : SDF.parse(lastMax);
        } catch(Exception e) {
            start = new Date(0);
        }
        // 2. 查这段时间内被改过的用户
        List<User> updated = userRepository.findByUpdateTimeAfter(start);
        if (!updated.isEmpty()) {
            Date newMax = updated.stream()
                    .map(User::getUpdateTime)
                    .max(Date::compareTo)
                    .orElse(start);

            // 3. tishi
            updated.forEach(user -> {
                log.info("用户 {} 被更新，积分已同步。", user.getUsername());
            });

            // 4. 更新最大时间
            redisTemplate.opsForValue().set(LAST_MAX_TIME_KEY, SDF.format(newMax), 1, TimeUnit.DAYS);
        }
        log.info("同步用户积分任务执行完成。");

    }

    // 每分钟执行一次
    @Scheduled(cron = "0 * * * * ?")
    public void anotherJob() {
        log.info("另一个定时任务执行中...");
        // 这里可以添加具体的逻辑
        // TODO
        log.info("另一个定时任务执行完成。");
    }


}
