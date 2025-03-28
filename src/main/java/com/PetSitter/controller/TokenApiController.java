package com.PetSitter.controller;

import com.PetSitter.config.CookieUtil;
import com.PetSitter.config.jwt.TokenProvider;
import com.PetSitter.config.oauth.OAuth2SuccessHandler;
import com.PetSitter.domain.Member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class TokenApiController {

    private final TokenProvider tokenProvider;

    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 리프레시 토큰 가져오기
        String refreshToken = CookieUtil.getCookieValue(request, OAuth2SuccessHandler.REFRESH_TOKEN_COOKIE_NAME);

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
        Member authentication = (Member) tokenProvider.getAuthentication(refreshToken).getPrincipal();

        String newAccessToken = tokenProvider.generateToken(authentication, OAuth2SuccessHandler.ACCESS_TOKEN_DURATION, oauthProvider);

        // 새 액세스 토큰을 쿠키에 저장
        CookieUtil.addCookie(response, OAuth2SuccessHandler.ACCESS_TOKEN_COOKIE_NAME, newAccessToken,
                (int) OAuth2SuccessHandler.ACCESS_TOKEN_DURATION.toSeconds());

        return ResponseEntity.ok("Access token refreshed");
    }
}
