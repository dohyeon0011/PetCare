package com.PetSitter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("dev")
@Configuration
public class WebImageConfigDev implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profile/**") // 디렉토리 내 파일들을 제공
                .addResourceLocations("file:/Users/idohyeon/petcare-uploads/profile/"); // 서버 내부 경로

        registry.addResourceHandler("/uploads/pets/**")
                .addResourceLocations("file:/Users/idohyeon/petcare-uploads/pets/");

        registry.addResourceHandler("/uploads/carelogs/**")
                .addResourceLocations("file:/Users/idohyeon/petcare-uploads/carelogs/");
    }
}
