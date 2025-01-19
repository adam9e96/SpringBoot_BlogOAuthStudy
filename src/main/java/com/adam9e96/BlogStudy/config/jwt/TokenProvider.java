package com.adam9e96.BlogStudy.config.jwt;

import com.adam9e96.BlogStudy.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 비밀 키를 생성하는 메서드.
     * properties 파일에 설정된 비밀 키 문자열을 바이트 배열로 변환한 후 SecretKey 객체를 생성하여 반환
     *
     * @return SecretKey 객체
     */
    private SecretKey getSecretKey() {
        log.info("가져옴 jwtProperties.getSecretKey() : {}", jwtProperties.getSecretKey());
        log.info("Generating SecretKey...");
        // 비밀 키를 byte 배열로 변환하고 SecretKey 객체를 생성
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());

//        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
//        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtProperties.getSecretKey()));
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("SecretKey 생성됨 : {}", secretKey);
        return secretKey;

    }

    /**
     * JWT 토큰을 생성하는 메서드
     *
     * @param user      토큰을 발급할 사용자 객체
     * @param expiredAt 토큰의 만료 기간
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(User user, Duration expiredAt) {

        // 1. 현재 시간 기준으로 토큰 만료 시간 설정
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiredAt.toMillis());

        return makeToken(expiryDate, user);
    }

    /**
     * JWT 토큰을 실제로 생성하는 메서드
     *
     * @param expiry 토큰의 만료날짜
     * @param user   토큰을 발급할 사용자 객체
     * @return 생성된 JWT 토큰 문자열
     */
    private String makeToken(Date expiry, User user) {
        Date now = new Date();
        String token = Jwts.builder()
                .header()
                .keyId("typ") // 헤더에 key ID 설정
                .type("JWT") // 헤더에 타입 설정 (JWT)
                .and()
                // 내용 iss : adam1123@gmail.com(properties 파일에서 설정한 값)
                .issuer(jwtProperties.getIssuer()) // "iss" : "adam6a@gmai.com"
                .issuedAt(now) // "iat" : "현재 시간"
                .expiration(expiry) // "exp" : "expiry"
                .subject(user.getEmail()) // "sub" : "유저의 이메일"
                .claim("id", user.getId()) // "id" : "유저의 ID"
                // 4. 서명 ( 비밀값을 사용하여 해시값을 생성)
                // 비밀값과 함께 해시 값을 HS256 방식으로 암호화
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                // 5. 컴팩트 (최종 토큰 문자열 생성)
                .compact();
        return token;
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
            log.info("validToken() | 토큰 유효성 검증 성공.");
            return true; // 예외가 발생하지 않으면 유효한 토큰
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            log.error("validToken() | 토큰 유효성 검증 실패: {}", e.getMessage());
            return false; // 우효화 하지 않은 토큰
        }
    }

    /**
     * JWT 토큰을 기반으로 Authentication 객체를 생성하는 메서드
     * UsernamePassswordAuthenticationToken 객체를 생성하여 반환
     *
     * @param token 인증에 사용할 JWT 토큰 문자열
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        log.info("getAuthentication() 시작");

        // 4. Claims에서 사용자 정보 추출
        Claims claims = getClaims(token);

        // 5. 권한 정보 설정
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        log.info("getAuthentication() | 권한: {}", authorities);

        // UserDetails 객체 생성 (빌더 패턴 사용)
        // UserDetails 를 구현한 user 엔티티를 사용하고 기에 Spring Security 에서 제공하는 User 객체를 사용 하지 않음
        /*
         * Spring Security 에서 제공하는 User 객체를 사용하는 경우
         *     UserDetails userDetails = User.builder()
         *             .username(claims.getSubject())  // 사용자 이메일
         *             .password("")                   // 비밀번호 빈 문자열 (이미 인증됨)
         *             .authorities(authorities)       // 권한 설정
         *             .build();
         */
        // UserDetails 객체 생성 1. 생성자 방식
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                claims.getSubject(), // username
                "",                  // password (빈 문자열)
                authorities          // 권한 목록
        );
        // UserDetails 객체 생성 2. 빌더 패턴
        /*
         * UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
         *         .username(claims.getSubject())  // 사용자 이메일
         *         .password("")                   // 비밀번호 빈 문자열 (이미 인증됨)
         *         .authorities(authorities)       // 권한 설정
         *         .build();
         */

        User user = User.builder()
                .email(claims.getSubject())    // 이메일(username)
                .password("")                  // 비밀번호 빈 문자열 (이미 인증됨)
                .build();

        // 6. UsernamePasswordAuthenticationToken 개체 생성 (인증 객체 생성)
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,   // principal (User 객체)
                        token,          // credentials (JWT 토큰)
                        authorities     // 권한 목록
                );


        // ========================================================================================================
        // 3,4 과정을 한번에 처리
//        return new UsernamePasswordAuthenticationToken(
//                new org.springframework.security.core.userdetails.User(
//                        claims.getSubject(), // 사용자 이메일을 principal 로 설정
//                        "", // 비밀번호는 빈 문자열로 설정 (이미 인증됨)
//                        authorities // 권한 설정
//                ),
//                token,  // credentials 로 토큰 자체를 설정
//                authorities // 권한 설정
//        );
        // ========================================================================================================

        log.info("인증 객체 생성 완료: {}", authentication);
        return authentication;
    }

    // 토큰 기반으로 유저 ID를 가져오는 메서드

    /**
     * JWT 토큰에서 사용자 ID를 추출하는 메서드
     *
     * @param token JWT 토큰 문자열
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        log.info("getUserId() | Getting user ID for token: {}", token);
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
        log.info("getClaims() | Getting claims for token: {}", token);
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey()) // 비밀값으로 복호화
                .build()
                .parseSignedClaims(token)
                .getPayload(); // 토큰에서 클레임 추출
//        return Jwts.parser() // 클레임 조회
//                .verifyWith(getSecretKey()) // SecretKey 설정
//                .build()
//                .parseSignedClaims(token)
//                .getPayload(); // 클레임 반환
        log.info("getClaims() | 클레임 추출 성공: {}", claims);
        return claims;
    }

}
