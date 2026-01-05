package com.ra.bakerysystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")          // Áp dụng cho tất cả API
                        .allowedOriginPatterns("*")        // Cho phép tất cả domain
                        .allowedMethods("*")        // Cho phép tất cả method (GET, POST, PUT, DELETE...)
                        .allowedHeaders("*")        // Cho phép tất cả header
                        .allowCredentials(true)     // Cho phép gửi cookie
                        .maxAge(3600);              // Thời gian cache preflight request (tính bằng giây)
            }
        };
    }
}
