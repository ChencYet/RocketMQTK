//package com.takeaway.config;
//
//import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConfigurationProperties(prefix = "rocketmq")
//public class RocketMQConfig {
//
//    // RocketMQ配置已在application.yml中完成
//    // 这里可以添加自定义的MQ配置
//
//    @Bean
//    @ConditionalOnMissingBean
//    public RocketMQTemplate rocketMQTemplate(@Autowired RocketMQProperties rocketMQProperties) {
//        // 可以在这里进行自定义配置
//        RocketMQTemplate template = new RocketMQTemplate();
//        return template;
//    }
//
//
//}
//
//
