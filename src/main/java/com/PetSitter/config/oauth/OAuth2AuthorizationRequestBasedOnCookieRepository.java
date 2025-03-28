package com.PetSitter.config.oauth;

import com.PetSitter.config.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * OAuth2 인증 요청(필요한 정보)을 세션이 아닌 쿠키에 저장하고 로드하는 데 사용되는 구현체(인증 요청과 관련된 상태를 저장할 저장소)
 * OAuth2의 정보를 가져오고 저장하는 로직(OAuth2 인증 요청 정보를 쿠키에 저장하고 관리)
 */
@Component
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 18000; // 5 hours

    /**
     * OAuth2 인증 요청 삭제
     * 인증 요청을 제거할 때, 쿠키에서 해당 정보를 삭제
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request); // 쿠키에서 인증 요청을 불러옴
    }

    /**
     * OAuth2 인증 요청 불러오기
     * 현재 요청과 관련된 인증 정보를 쿠키에서 찾아서 가져옴
     * 로그인 성공 후, 로그인 요청 전에 사용자가 있던 위치로 리디렉트할 수 있도록 복원
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class); // 쿠키에서 인증 요청을 디시리얼라이즈
    }

    /**
     * OAuth2 인증 요청 정보를 쿠키에 저장
     * 사용자가 OAuth2 로그인을 시도하면, OAuth2AuthorizationRequest 객체를 쿠키에 저장
     * 이렇게 하면 인증이 완료된 후 원래 요청을 복원 가능
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response); // null이면 쿠키 삭제
            return;
        }

        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
    }

    // 쿠키에서 인증 요청 삭제
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

}
