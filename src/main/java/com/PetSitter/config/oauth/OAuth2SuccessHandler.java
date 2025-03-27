package com.PetSitter.config.oauth;

import com.PetSitter.config.CookieUtil;
import com.PetSitter.config.jwt.TokenProvider;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.service.Member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofHours(3); // 리프레시 토큰 유효기간
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);   // 액세스 토큰 유효기간
    public static final String REDIRECT_PATH = "/pets-care/main";  // 리다이렉트할 경로

    private final TokenProvider tokenProvider;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository auth2AuthorizationRequestRepository;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Object member = memberService.findById((Long) oAuth2User.getAttributes().get("id"));

        // 리프레시 토큰 생성
        String refreshToken = tokenProvider.generateToken((Member) member, REFRESH_TOKEN_DURATION);

        // 리프레시 토큰을 쿠키에 저장
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성
        String accessToken = tokenProvider.generateToken((Member) member, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 리프레시 토큰을 쿠키에 저장하는 메서드
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();  // 쿠키의 만료 기간 설정
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME); // 기존 쿠키 삭제
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge); // 새 쿠키 추가
    }

    // 인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 액세스 토큰을 URL 쿼리 파라미터에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}

