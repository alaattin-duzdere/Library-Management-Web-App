package com.example.library_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Örn: Tarayıcı http://localhost:8080/media/images/abc.jpg istediğinde...
        registry
                .addResourceHandler("/media/images/**")
                // ...Spring Boot, sunucudaki "file:./uploads/images/" klasörünün altına bakar.
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
