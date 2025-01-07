package com.adam9e96.BlogStudy.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Getter
public class JwtFactory {
    private String subject = "test@gmail.com";
    private Date issueAt = new Date();
    private Date expiration = new Date(issueAt.getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = emptyMap();

    // 빌더 패턴을 사용해 설정이 필요한 데이터만 선택 설정
    @Builder
    public JwtFactory(String subject, Date issueAt, Date expiration,
                      Map<String, Object> claims) {
        this.subject = subject != null ? subject : this.subject;
        this.issueAt = issueAt != null ? issueAt : this.issueAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    public static JwtFactory withDefaultValues() {
        return JwtFactory.builder().build();
    }

    // jjwt 라이브러리를 사용해 JWT 토큰 생성
    public String createToken(JwtProperties jwtProperties) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(subject)
                .issuedAt(issueAt)
                .expiration(expiration)
                .claims(claims)
                .setHeaderParam("typ", "JWT")
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
