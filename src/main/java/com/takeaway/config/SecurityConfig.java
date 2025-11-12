package com.takeaway.config;

import com.takeaway.util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig  {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // 创建AuthenticationManager bean 用于身份验证
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests(auth -> auth
                // 1. 业务放行
                .requestMatchers("/takeaway/user/login","/takeaway/user/register").permitAll()
                // 2. 接口文档 HTML/静态资源
                .requestMatchers("/doc.html","/swagger-ui/**","/knife4j/**").permitAll()
                // 3. 关键：OpenAPI JSON 源
                .requestMatchers("/v3/api-docs","/v3/api-docs/**","/swagger-resources/**","/webjars/**").permitAll()
                // 4. 其他请求都需要认证
                .requestMatchers("/api/ws", "/api/ws/**").permitAll()
                .anyRequest().authenticated()
        ).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 禁用会话

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
