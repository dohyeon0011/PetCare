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
                response.addCookie(cookie); // 응답에 삭제된 쿠키 추가
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

    // 쿠키 이름으로 쿠키 값을 가져오는 메서드
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        // 요청에서 모든 쿠키를 가져옴
        Cookie[] cookies = request.getCookies();

        // 쿠키 배열이 null이 아니고, 해당 쿠키 이름이 존재하는지 확인
        if (cookies != null) {
            // 쿠키 배열을 순회하면서 특정 이름의 쿠키를 찾음
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue(); // 해당 쿠키 값을 반환
                }
            }
        }

        // 쿠키가 없으면 null 반환
        return null;
    }

    // 객체를 Base64로 직렬화하여 쿠키에 저장할 수 있도록 변환
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    // Base64 인코딩된 쿠키 값을 역직렬화해 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
