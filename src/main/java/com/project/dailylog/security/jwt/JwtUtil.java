package com.project.dailylog.security.jwt;

import com.project.dailylog.model.dto.LoginDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final Key key;
    private final long accessTokenValidity = 15 * 60 * 1000L;
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000L;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    public String createAccessToken(LoginDTO loginUser) {
        Claims claims = Jwts.claims();
        claims.put("email", loginUser.getEmail());
        claims.put("role", loginUser.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(loginUser.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT에서 클레임 추출
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 사용자 ID 추출
    public String getUserId(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * JWT 생성
     * @param loginUser
     * @param expireTime
     * @return JWT String
     */

    /**
     * Token에서 User ID 추출
     * @param token
     * @return User ID

    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    } */

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.warn("Invalid JWT Token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT Token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT Token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
