package com.ra.bakerysystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${path-upload}")
    private String uploadDir;
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
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                System.out.println("Upload Directory: " + uploadDir);
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + uploadDir + "/");
            }
        };

    }



}
