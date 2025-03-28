package com.PetSitter.config.jwt;

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
}
