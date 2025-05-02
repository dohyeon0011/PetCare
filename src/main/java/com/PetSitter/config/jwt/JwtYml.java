package com.PetSitter.config.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration")
public class JwtYml {
    private OAuth2Client google;
    private OAuth2Client kakao;
    private OAuth2Client naver;

    @Getter
    @Setter
    public static class OAuth2Client {
        private String clientId;
        private String clientSecret;
        private String issuer;  // 각 OAuth2 제공자에 대한 issuer
    }

    @PostConstruct
    public void init() {
        // 각 소셜 로그인 제공자에 대한 정보를 출력하여 값이 제대로 로딩되었는지 확인
        System.out.println("Google Client ID: " + google.getClientId());
        System.out.println("Google Secret Key: " + google.getClientSecret());
        System.out.println("Kakao Client ID: " + kakao.getClientId());
        System.out.println("Kakao Secret Key: " + kakao.getClientSecret());
        System.out.println("Naver Client ID: " + naver.getClientId());
        System.out.println("Naver Secret Key: " + naver.getClientSecret());
    }
}
