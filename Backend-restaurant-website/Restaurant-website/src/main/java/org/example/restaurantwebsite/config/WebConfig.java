package org.example.restaurantwebsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешить CORS для всех эндпоинтов
                //.allowedOrigins("http://127.0.0.1:5500") // Разрешить доступ только с этого адреса
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
