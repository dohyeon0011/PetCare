package com.PetSitter.config;

import com.PetSitter.config.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *  Access Token을 재발급 받기 위한 Token
 *  필터는 실제로 각종 요청이 요청을 처리하기 위한 로직으로 전달되기 전후에 URL 패턴에 맞는 모든 요청을 처리하는 기능을 제공.
 *  요청이 오면 헤더값을 비교해서 토큰이 있는지 확인하고 유효 토큰이라면 시큐리티 콘텍스트 홀더에 인증 정보를 저장.
 */
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer";

    private final TokenProvider tokenProvider;  // JWT 검증 및 사용자 정보 추출을 담당하는 클래스

    /**
     *  요청 헤더에서 키가 'Authorization'인 필드의 값을 가져온 다음 토큰의 접두사 Bearer를 제외한 값을 얻는다.
     *  만약 값이 null이거나 Bearer로 시작하지 않으면 null을 반환한다. 이어서 가져온 토큰이 유효한 지 확인하고,
     *  유효하다면 인증 정보를 관리하는 시큐리티 컨텍스트에 인증 정보를 설정. 인증 정보가 설정된 이후에 컨텍스트 홀더에서
     *  getAuthentication() 메소드를 사용해 인증 정보를 가져오면 유저 객체가 반환된다.
     *  유저 객체에는 유저 이름(username)과 권한 목록(authorities)과 같은 인증 정보가 포함됨.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. HTTP 요청에서 JWT 토큰을 추출
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        String token = resolveToken(authorizationHeader);

        // 요청 헤더나 본문에서 제공자 정보 받아오기 (google, kakao, naver)
//        String oauthProvider = request.getHeader("provider");  // google, kakao, naver

        // 2. 토큰이 존재하고 유효하다면
        if (token != null && tokenProvider.validateToken(token)) {
            // 3. 토큰으로 사용자 정보 가져오기
            Authentication authentication = tokenProvider.getAuthentication(token);

            // 4. 해당 사용자 정보를 SecurityContext에 저장
            // 5. 사용자 인증 정보 설정
            // 6. SecurityContextHolder에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // 요청에서 토큰을 추출하는 메서드
    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());  // "Bearer "를 제외한 실제 토큰 반환
        }
        return null;
    }
}
