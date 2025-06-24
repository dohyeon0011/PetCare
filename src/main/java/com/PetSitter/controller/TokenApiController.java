package com.PetSitter.controller;

import com.PetSitter.config.CookieUtil;
import com.PetSitter.config.jwt.TokenProvider;
import com.PetSitter.config.oauth.OAuth2SuccessHandler;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.service.Member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class TokenApiController {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);

    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Operation(summary = "액세스 토큰 갱신", description = "액세스 토큰 갱신 API")
    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException {
        // 쿠키에서 리프레시 토큰 가져오기
        String refreshToken = CookieUtil.getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);

        // 요청 헤더나 본문에서 제공자 정보 받아오기 (ex: google, kakao, naver)
        String oauthProvider = tokenProvider.extractProviderFromToken(refreshToken);
//        String oauthProvider = request.getHeader("provider");  // google, kakao, naver

        if (oauthProvider == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("OAuth provider is missing");
        }

        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid refresh token");
        }

        // 리프레시 토큰이 유효하면 새 액세스 토큰 생성
        Object principal = tokenProvider.getAuthentication(refreshToken).getPrincipal();
        log.info("Principal class: " + principal.getClass());

        Member member;
        if (principal instanceof User) {    // org.springframework.security.core.userdetails.User
            String username = ((User) principal).getUsername();
            member = memberService.findByUsername(username);
        } else if (principal instanceof MemberDetails) {    // UserDetails 구현체
            MemberDetails memberDetails = (MemberDetails) principal;
            member = memberDetails.getMember();
        } else if (principal instanceof CustomOAuth2User) { // OAuth2User 구현체
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            member = customOAuth2User.getMember();
        } else {
            throw new RuntimeException("토큰 갱신 중 오류. 알 수 없는 principal 타입: " + principal.getClass());
        }
        String newAccessToken = tokenProvider.generateToken(member, OAuth2SuccessHandler.ACCESS_TOKEN_DURATION, oauthProvider);

        log.info("Old Access Token: " + CookieUtil.getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME));
        log.info("New Access Token: " + newAccessToken);

        // 새로운 토큰 쿠키에 추가
        addAccessTokenToCookie(request, response, newAccessToken);

        return ResponseEntity.ok("Access token refreshed");
    }

    private static void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String newAccessToken) {
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE_NAME); // 기존 쿠키(액세스 토큰) 삭제
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, newAccessToken,   // 새 액세스 토큰을 쿠키에 저장
                (int) ACCESS_TOKEN_DURATION.toSeconds());
    }
}
