package com.example.employeemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Quy tắc cụ thể đặt TRƯỚC: /employees/new, /employees/{id}/edit ... cũng khớp mẫu /employees/*
                        .requestMatchers(HttpMethod.GET, "/employees/new").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/employees/*/edit").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/employees/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/employees", "/employees/*").hasAnyRole("USER", "ADMIN")
                        // Xem danh sách, tìm kiếm, chi tiết: cả 3 role
                        // (riêng tìm theo salary sẽ được kiểm tra thêm trong EmployeeController.search)
                        .requestMatchers(HttpMethod.GET, "/employees", "/employees/search", "/employees/*")
                        .hasAnyRole("GUEST", "USER", "ADMIN")
                        // Các trang còn lại (/, /reports/**...): chỉ cần đã đăng nhập
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())   // form /login mặc định của Spring Security
                .logout(Customizer.withDefaults());     // POST /logout (kèm CSRF token)

        return http.build();   // CSRF mặc định bật
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}