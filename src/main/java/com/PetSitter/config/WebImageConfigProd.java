package com.PetSitter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebImageConfigProd implements WebMvcConfigurer { // 배포 환경 이미지 파일 경로 설정

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로필 이미지 경로
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations("file:/home/ubuntu/petscare/uploads/profile/");

        // 펫 이미지 경로
        registry.addResourceHandler("/uploads/pets/**")
                .addResourceLocations("file:/home/ubuntu/petscare/uploads/pets/");

        // 케어 로그 이미지 경로
        registry.addResourceHandler("/uploads/carelogs/**")
                .addResourceLocations("file:/home/ubuntu/petscare/uploads/carelogs/");
    }
}
