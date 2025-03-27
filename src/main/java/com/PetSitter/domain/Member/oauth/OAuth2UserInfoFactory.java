package com.PetSitter.domain.Member.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

/**
 * OAuth2UserInfoFactory가 소셜 로그인 제공자에 맞는 클래스(GoogleOAuth2UserInfo, KakaoOAuth2UserInfo, NaverOAuth2UserInfo)를 자동으로 선택
 */
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if ("kakao".equals(registrationId)) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if ("naver".equals(registrationId)) {
            return new NaverOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }
    }
}
