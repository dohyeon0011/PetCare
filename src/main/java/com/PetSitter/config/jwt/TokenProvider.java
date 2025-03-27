package com.PetSitter.config.jwt;

import com.PetSitter.domain.Member.Member;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Duration;
import java.util.Date;

@Component
public class TokenProvider {

    private final String SECRET_KEY = "my-secret-key";

    // 액세스 토큰 생성
    public String generateToken(Member member, Duration duration) {
        return Jwts.builder()
                .setSubject(member.getLoginId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + duration.toMillis()))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

}
