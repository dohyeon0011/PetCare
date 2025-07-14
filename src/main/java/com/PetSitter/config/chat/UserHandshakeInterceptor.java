package com.PetSitter.config.chat;

import com.PetSitter.config.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

// WebSocket 인증 정보 등록 인터셉터
// WebSocket 핸드셰이드 단계에서 인증 정보를 확인해서 WebSocket 핸드셰이크에 HTTP 세션이나 JWT 토큰을 넘겨 인증하고,
// 인증된 사용자 정보를 WebSocket 세션에 담음. 그럼 이제 @AuthenticationPrincipal이나 simplerUser로 인증 정보 조회 가능해짐.
@RequiredArgsConstructor
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // 기존 세션 인증 정보가 있다면 그대로 사용
                attributes.put("user", authentication);
                return true;
            }

            // 세션 인증이 없으면 JWT 쿠키 검사
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("access_token".equals(cookie.getName())) {
                        String token = cookie.getValue();

                        if (tokenProvider.validateToken(token)) {
                            Authentication auth = tokenProvider.getAuthentication(token);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            attributes.put("user", auth);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
            // 인증 실패 시 연결 차단 or 허용 여부 결정
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
