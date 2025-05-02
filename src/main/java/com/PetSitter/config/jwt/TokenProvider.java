package com.PetSitter.config.jwt;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.PetSitter.config.jwt.JwtYml.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private final JwtYml jwtYml;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용

    // 액세스 토큰 생성
    public String generateToken(Member member, Duration duration, String oauthProvider) throws NoSuchAlgorithmException {
        OAuth2Client oauth2Client = getOAuth2Client(oauthProvider);

        // OAuth2 Secret Key를 Base64 인코딩(네이버 로그인 시 키가 80비트 밖에 안돼서 오류가 발생: HS256 알고리즘에 쓰이는 키의 길이가 256비트 이상이어야 함.)
//        String base64Secret = Base64.getEncoder().encodeToString(oauth2Client.getClientSecret().getBytes());

        // OAuth2 Secret Key를 SHA-256 해싱하여 키 길이 보장(256비트 길이로 변환)
        byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(oauth2Client.getClientSecret().getBytes(StandardCharsets.UTF_8));
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setIssuer(oauth2Client.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + duration.toMillis()))
                .setSubject(member.getLoginId())    // 내용 sub(토큰 제목) : 유저 로그인 ID
                .claim("id", member.getId())  // 클레임 id : 유저 ID
                .claim("role", member.getRole()) // 클레임 role : 유저 역할
                .claim("provider", oauthProvider)
                .signWith(key)
//                .signWith(SignatureAlgorithm.HS256, base64Secret) // 해당 oauth2 제공자의 시크릿 키
                .compact();
    }

    // OAuth2 제공자 찾기
    private OAuth2Client getOAuth2Client(String oauthProvider) {
        OAuth2Client oauth2Client;

        // OAuth2 제공자에 따라 맞는 정보 갖고 오기
        switch (oauthProvider.toLowerCase()) {
            case "google":
                oauth2Client = jwtYml.getGoogle();
                break;
            case "kakao":
                oauth2Client = jwtYml.getKakao();
                break;
            case "naver":
                oauth2Client = jwtYml.getNaver();
                break;
            default:
                throw new IllegalArgumentException("Unknown OAuth2 provider: " + oauthProvider);
        }

        return oauth2Client;
    }

    // JWT 유효성 검사
    public boolean validateToken(String token) {
        try {
            // 1. provider 먼저 추출
            String provider = extractProviderWithoutSignature(token);
            String secretKey = getSecretKeyByProvider(provider);

            // 2. 올바른 secretKey로 JWT 서명 검증
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8));
            Key key = Keys.hmacShaKeyFor(keyBytes); // HMAC-SHA256 서명용 키로 변환

            // 3. 올바른 secretKey로 JWT 서명 검증(변환된 key를 사용하여 JWT 서명 검증을 진행)
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);

            return true;
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않는 토큰
            log.error("Token 유효성 검사 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    // 'provider' OAuth2 제공자 추출(서명 검증 없이 provider 추출 (Base64 디코딩 후 JSON 파싱))
    public String extractProviderWithoutSignature(String token) {
        try {
            // JWT의 두 번째 부분 (Payload) 추출
            String payload = token.split("\\.")[1];
            log.info("Decoded Payload: " + payload); // 페이로드 값 출력

            // Base64 URL 디코딩
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));
            log.info("Decoded Payload (After Base64 Decode): " + decodedPayload); // Base64 디코딩 후 값 출력

            // JSON을 Map으로 변환
            Map<String, Object> claims = objectMapper.readValue(decodedPayload, Map.class);
            log.info("Decoded Claims: " + claims); // 디코딩된 claims 출력
            log.info("claims provider=" + claims.get("provider"));

            // provider 값 반환
            return (String) claims.get("provider");
        } catch (Exception e) {
            log.error("Error splitting or decoding token: " + e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    // OAuth2 제공자의 시크릿 키를 찾기
    public String getSecretKeyByProvider(String provider) {
        switch (provider.toLowerCase()) {
            case "google":
                return jwtYml.getGoogle().getClientSecret();
            case "kakao":
                return jwtYml.getKakao().getClientSecret();
            case "naver":
                return jwtYml.getNaver().getClientSecret();
            default:
                throw new IllegalArgumentException("Unknown OAuth2 provider: " + provider);
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메소드
    public Authentication getAuthentication(String token) {
        // 1. provider 먼저 추출
        String provider = extractProviderWithoutSignature(token);
        String secretKey = getSecretKeyByProvider(provider);

        // 2. 올바른 secretKey로 JWT 서명 검증 및 클레임 추출
        Claims claims = getClaims(token, secretKey);

        SimpleGrantedAuthority authority = getAuthorityFromClaims(claims);

        // org.springframework.security.core.userdetails.User로 반환(여기서 클레임 조회 후 클레임 getSubject() 해서 LoginId 찾고 바로 회원 직빵으로 조회도 가능함.)
        return new UsernamePasswordAuthenticationToken(
                new User(claims.getSubject(), "", Collections.singleton(authority)),
                token,
                Collections.singleton(authority)
        );
    }

    // 클레임 조회(토큰과 시크릿 키로)
    private static Claims getClaims(String token, String secretKey){
        // OAuth2 Secret Key를 SHA-256 해싱하여 키 길이 보장(256비트 길이로 변환)
        byte[] keyBytes = null;
        try {
            keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            log.error("OAuth2 Secret Key SHA-256 해싱 과정 오류: " + e.getMessage());
            throw new RuntimeException(e);
        }
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parser()
                .setSigningKey(key)  // 새로 생성한 키를 사용
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }

    /*private Claims getClaims(String token, String oauthProvider) { // 클레임 조회
        OAuth2Client oauth2Client = getOAuth2Client(oauthProvider);

        return Jwts.parser()
                .setSigningKey(oauth2Client.getClientSecret())
                .parseClaimsJws(token)  // JWT 생성 시 설정한 시크릿 키와 일치한 지 확인하고 파싱
                .getBody();
    }*/

    // JWT 클레임에서 역할 정보를 추출하는 메소드
    private SimpleGrantedAuthority getAuthorityFromClaims(Claims claims) {
        String role = (String) claims.get("role"); // "role" 클레임에서 역할 정보 추출

        if (role != null && !role.isEmpty()) {
            return new SimpleGrantedAuthority("ROLE_" + role); // "ROLE_" 접두사 붙여서 반환
        }

        // 기본 역할 설정
        return new SimpleGrantedAuthority("ROLE_CUSTOMER");
    }

    /**
     * 문제점
     *  provider 값을 꺼내려면 setSigningKey(secretKey)로 JWT를 먼저 해석해야 함.
     *  근데 setSigningKey(secretKey)를 하려면 어떤 제공자인지(provider)를 먼저 알아야 함.
     *  즉, JWT를 해석하려면 provider가 필요하고, provider를 꺼내려면 JWT를 해석해야 하는 순환문제 발생!
     *
     *  그래서 먼저 시그니처 검증 없이 provider만을 토큰에서 추출하고, 검증하기
     */
    public String extractProviderFromToken(String token) {
        String provider = extractProviderWithoutSignature(token); // 먼저 provider 추출
        String secretKey = getSecretKeyByProvider(provider); // provider에 맞는 secret key 찾기

        Claims claims = getClaims(token, secretKey);
        log.info("클레임 조회 성공: " + claims);
        log.info("클레임 소셜 제공자 조회: " + claims.get("provider", String.class));

        return claims.get("provider", String.class);
    }
}
