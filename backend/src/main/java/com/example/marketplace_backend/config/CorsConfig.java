package com.example.marketplace_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global configuration for Cross-Origin Resource Sharing (CORS).
 * Allows the frontend application (on localhost:3000/3001) to communicate
 * with this backend API (on localhost:8080).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS configuration to all API endpoints

                // Allow the common local development ports for Next.js/React
                .allowedOrigins("http://localhost:3000", "http://localhost:3001")

                // Allow the necessary HTTP methods, including OPTIONS for preflight requests
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // Allow all headers to be sent in the request (Authorization, Content-Type, etc.)
                .allowedHeaders("*")

                // CRUCIAL: Must be true to allow cookies, Authorization headers, and session info
                .allowCredentials(true)

                // Cache the CORS configuration for 1 hour to reduce preflight requests
                .maxAge(3600);
    }
}