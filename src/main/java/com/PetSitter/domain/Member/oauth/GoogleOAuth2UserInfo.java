package com.PetSitter.domain.Member.oauth;

import java.util.Map;

/**
 * 구글 사용자 정보 제공 데이터 스펙
 * {
 *   "sub": "123456789",
 *   "name": "홍길동",
 *   "email": "hong@gmail.com",
 *   "picture": "https://lh3.googleusercontent.com/profile.jpg"
 * }
 */
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub"); // 구글은 "sub" 필드를 ID로 사용
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
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
