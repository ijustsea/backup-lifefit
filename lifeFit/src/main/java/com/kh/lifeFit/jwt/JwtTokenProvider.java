package com.kh.lifeFit.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpireMs = 1000L * 60 * 60 * 12; // 개발 편의를 위해 만료시간 12시간으로 연장

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //Acess Token generate
    public String createAccessToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpireMs);
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //Token Valid
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
        }
        return false;
    }

    /**
     * [공통 메서드] 토큰 파싱 및 클레임 추출
     * 만료된 토큰이어도 Claims를 반환하여 시스템 중단을 방지함
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 여기가 핵심: 만료 에러가 나도 내부 데이터는 반환함
            return e.getClaims();
        }
    }
    //토큰에서 이메일 추출
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }
    //토큰에서 userId 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }
    //토큰에서 role 추출
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }



}
