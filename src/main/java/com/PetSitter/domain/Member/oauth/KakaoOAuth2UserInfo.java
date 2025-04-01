package com.PetSitter.domain.Member.oauth;

import java.util.Map;

/**
 * 카카오 사용자 정보 제공 데이터 스펙
 * {
 *  *   "id": 987654321,
 *  *   "kakao_account": {
 *  *     "profile": { "nickname": "길동이" },
 *  *     "email": "gildong@kakao.com"
 *        "phone_number": "+82 10-1234-5678"
 *  *   }
 *  * }
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id")); // 카카오는 "id" 필드를 ID로 사용
    }

    @Override
    public String getName() {
        Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile");
        return profile != null ? (String) profile.get("nickname") : "사용자";
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        return account != null ? (String) account.get("email") : null;
    }

    @Override
    public String getPhoneNumber() {
        return (String) ((Map<String, Object>) attributes.get("kakao_account")).get("phone_number");
    }
}
