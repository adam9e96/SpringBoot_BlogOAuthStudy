package com.adam9e96.BlogStudy.config.jwt;

import com.adam9e96.BlogStudy.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 비밀 키를 생성하는 메서드.
     * properties 파일에 설정된 비밀 키 문자열을 바이트 배열로 변환한 후 SecretKey 객체를 생성하여 반환
     *
     * @return SecretKey 객체
     */
    private SecretKey getSecretKey() {
        // 비밀 키를 byte 배열로 변환하고 SecretKey 객체를 생성
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰을 생성하는 메서드
     *
     * @param user      토큰을 발급할 사용자 객체
     * @param expiredAt 토큰의 만료 기간
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT 토큰 생성 메서드

    /**
     * JWT 토큰을 실제로 생성하는 메서드
     *
     * @param expiry 토큰의 만료날짜
     * @param user   토큰을 발급할 사용자 객체
     * @return 생성된 JWT 토큰 문자열
     */
    private String makeToken(Date expiry, User user) {
        Date now = new Date();
        return Jwts.builder()
                .header()
                .keyId("typ") // 헤더에 key ID 설정
                .type("JWT") // 헤더에 타입 설정 (JWT)
                .and()
//                .setHeaderParam("type","jwt") // 헤더 typ : JWT (더이상 사용되지 않음)
                // 내용 iss : adam1123@gmail.com(properties 파일에서 설정한 값)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now) // 내용 iat : 현재 시간
                .expiration(expiry) // 내용 exp : expiry 멤버 변숫값
                .subject(user.getEmail()) // 내용 sub : 유저의 이메일
                .claim("id", user.getId()) // 클레임 id : 유저 ID
                // 서명 : 비밀값과 함께 해시 값을 HS256 방식으로 암호화
                .signWith(getSecretKey(), Jwts.SIG.HS256)
//                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 더이상 안씀
                .compact(); // 최종 토큰 문자열 생성
    }

    /**
     * JWT 토큰의 유효성을 검증하는 메서드
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 유요한 토큰이면 true, 그렇지 않으면 false
     */
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey()) // 비밀값으로 복호화
                    .build()
                    .parseSignedClaims(token); // 토큰 파싱 및 검증

            return true; // 예외가 발생하지 않으면 유효한 토큰
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false; // 우효화 하지 않은 토큰
        }
    }

    /**
     * JWT 토큰을 기반으로 Authentication 객체를 생성하는 메서드
     *
     * @param token 인증에 사용할 JWT 토큰 문자열
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new
                SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core
                .userdetails.User(
                claims.getSubject(), // 사용자 이메일을 principal 로 설정
                "", // 비밀번호는 빈 문자열로 설정 (이미 인증됨)
                authorities // 권한 설정
        ),
                token,  // credentials 로 토큰 자체를 설정
                authorities // 권한 설정
        );
    }

    // 토큰 기반으로 유저 ID를 가져오는 메서드

    /**
     * JWT 토큰에서 사용자 ID를 추출하는 메서드
     *
     * @param token JWT 토큰 문자열
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token); // 토큰에서 클레임 추출
        return claims.get("id", Long.class); // "id" 클레임에서 Long 타입으로 사용자 ID 추출
    }


    /**
     * JWT 토큰에서 사용자 ID를 추출하는 메서드
     *
     * @param token JWT 토큰 문자열
     * @return Claims 객체
     */
    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .verifyWith(getSecretKey()) // SecretKey 설정
                .build()
                .parseSignedClaims(token)
                .getPayload(); // 클레임 반환
    }

}
