package com.PetSitter.config.oauth;

import com.PetSitter.config.CookieUtil;
import com.PetSitter.config.jwt.TokenProvider;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.service.Member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * OAuth2 로그인 성공 핸들러
 * OAuth2 로그인 성공 시 JWT 토큰을 생성하고 쿠키에 저장
 * 기존 세션 기반 인증이 아닌 JWT 기반 인증을 처리하기 위한 역할 수행
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofHours(3); // 리프레시 토큰 유효기간
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);   // 액세스 토큰 유효기간
    public static final String REDIRECT_PATH = "/pets-care/main";  // 리다이렉트할 경로

    private final TokenProvider tokenProvider;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository auth2AuthorizationRequestRepository;
    private final MemberRepository memberRepository;

    /**
     *      로그인 성공 시 onAuthenticationSuccess() 실행
     *      CustomOAuth2User에서 사용자 정보를 가져와 데이터베이스에서 사용자 조회
     *      TokenProvider를 이용해 accessToken과 refreshToken 생성
     *      생성된 토큰을 쿠키에 저장 (addAccessTokenToCookie(), addRefreshTokenToCookie())
     *      OAuth2 로그인 관련 설정값 및 인증 쿠키 삭제 (clearAuthenticationAttributes())
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        log.info("OAuth2 로그인 성공: {}", authentication.getName());
        log.info("OAuth2 User: {}", oAuth2User);
        log.info("OAuth2 User Attributes: {}", oAuth2User.getAttributes());

        // OAuth2 공급자 확인
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        String loginId = getLoginIdFromOAuth2User(provider, oAuth2User); // 리소스 서버에 맞는 LoginId 추출
        log.info("OAuth2 User LOGINID: {}", loginId);

        // 회원 조회(활동 중인 유저만)
        Member member = memberRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new NoSuchElementException("Error, onAuthenticationSuccess(): 회원 정보 조회 실패"));

        // 리프레시 토큰 생성
        String refreshToken = null;

        try {
            refreshToken = tokenProvider.generateToken(member, REFRESH_TOKEN_DURATION, provider);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("리프레시 토큰 생성 과정 오류 발생: " + e.getMessage());
        }

        // 리프레시 토큰을 쿠키에 저장
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성
        String accessToken = null;

        try {
            accessToken = tokenProvider.generateToken(member, ACCESS_TOKEN_DURATION, provider);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("액세스 토큰 생성 과정 오류 발생: " + e.getMessage());
        }

        // 액세스 토큰을 쿠키에 저장
        addAccessTokenToCookie(request, response, accessToken);
//        String targetUrl = getTargetUrl(accessToken); // 액세스 토큰을 URL 쿼리 파라미터에 추가(이 방식은 SSR에서 로컬 혹은 세션 스토리지에 저장할 때 클라이언트에서 JS로 저장할 때 사용)

        // 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, REDIRECT_PATH);
    }

    // 리소스 서버에 맞는 LoginId 추출
    private static String getLoginIdFromOAuth2User(String provider, CustomOAuth2User oAuth2User) {
        // provider별로 LoginID 필드 결정
        String loginId;

        switch (provider.toLowerCase()) {
            case "google":
                loginId = (String) oAuth2User.getAttributes().get("sub");
                break;
            case "kakao":
                loginId = String.valueOf(oAuth2User.getAttributes().get("id"));
                break;
            case "naver":
                Map<String, Object> naverResponse = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                loginId = naverResponse != null ? (String) naverResponse.get("id") : null;
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 OAuth2 공급자입니다: " + provider);
        }

        // ID가 정상적으로 가져와졌는지 확인
        if (loginId == null) {
            throw new NoSuchElementException("Error, onAuthenticationSuccess(): OAuth2 LoginID 조회 실패");
        }
        return loginId;
    }

    // 리프레시 토큰을 쿠키에 저장하는 메서드
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();  // 쿠키의 만료 기간 설정
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME); // 기존 쿠키 삭제
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge); // 새 쿠키 추가
    }

    // 액세스 토큰을 쿠키에 저장하는 메서드
    private void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds();  // 쿠키의 만료 기간 설정
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE_NAME); // 기존 쿠키 삭제
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, cookieMaxAge); // 새 쿠키 추가
    }

    // 인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        auth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 액세스 토큰을 URL 쿼리 파라미터에 추가(이 방식은 SSR에서 로컬 혹은 세션 스토리지에 저장할 때 클라이언트에서 JS로 저장할 때 사용)
    /*private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }*/
}

