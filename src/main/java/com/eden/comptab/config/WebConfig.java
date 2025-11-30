package com.eden.comptab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Autoriser toutes les routes
                .allowedOriginPatterns("*") // Autoriser toutes les origines (Pour le dev)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Verbes autorisés
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}


