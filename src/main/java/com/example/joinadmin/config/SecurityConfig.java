package com.example.joinadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API이므로 CSRF 비활성화
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/users/register", "/api/users/health").permitAll() // 회원가입은 인증 없이 허용
                .anyRequest().authenticated() // 나머지는 인증 필요
            )
            .httpBasic(httpBasic -> {}); // Basic Auth 활성화 (관리자 API용)
        
        return http.build();
    }
}