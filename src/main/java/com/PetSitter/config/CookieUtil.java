package com.PetSitter.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

// 쿠키 관리 클래스
public class CookieUtil {

    // 쿠키 삭제(쿠키의 이름을 받아 쿠키 삭제)
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                cookie.setPath("/");  // 모든 경로에서 쿠키를 삭제
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    // 쿠키 추가(요청값(이름, 값, 만료 기간)을 바탕으로 HTTP 응답에 쿠키 추가)
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");       // 모든 경로에서 사용 가능
        cookie.setHttpOnly(true);  // HttpOnly로 설정
        cookie.setSecure(false);    // HTTP에서도 전송 가능(HTTPS 에서만 가능 X)
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // 객체를 직렬화해 쿠키의 값으로 들어갈 값으로 변환
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키를 역직렬화해 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
