/*
package com.PetSitter.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (httpServletRequest.getRequestURI().equals("/logout")) {
            // 로컬 개발 환경에서는 secure=false로 설정
            httpServletResponse.setHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Secure=false; Max-Age=0; SameSite=None");
            httpServletResponse.sendRedirect("/pets-care/main");  // 명시적 리다이렉트
            return;
        }

        chain.doFilter(request, response);
    }
}
*/
