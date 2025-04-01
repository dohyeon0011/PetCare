package com.PetSitter.domain.Member.oauth;

import java.util.Map;

/**
 * 네이버 사용자 정보 제공 데이터 스펙
 * {
 *   "resultcode": "00",
 *   "message": "success",
 *   "response": {
 *     "id": "abcdef123456",
 *     "name": "홍길동",
 *     "email": "hong@naver.com",
 *     "profile_image": "https://profile-phinf.pstatic.net/profile.jpg",
 *     "mobile": "010-1234-5678",
 *     "mobile_e164": "+82-10-1234-5678"
 *   }
 * }
 */
public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("response")); // 네이버는 "response" 안에 유저 정보가 있음
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getPhoneNumber() {
        return (String) attributes.get("mobile");
    }
}
