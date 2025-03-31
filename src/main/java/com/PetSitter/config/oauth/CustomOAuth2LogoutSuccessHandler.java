package com.PetSitter.config.oauth;

import com.PetSitter.config.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomOAuth2LogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CookieUtil.deleteCookie(request, response, "access_token");
        CookieUtil.deleteCookie(request, response, "refresh_token");

        // OAuth2 토큰을 명시적으로 제거
        SecurityContextHolder.clearContext(); // 시큐리티 콘텍스트에서 인증 정보 제거

        response.sendRedirect("/pets-care/main");
    }
}
