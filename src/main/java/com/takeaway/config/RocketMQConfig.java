package com.takeaway.config;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    // RocketMQ配置已在application.yml中完成
    // 这里可以添加自定义的MQ配置
}


