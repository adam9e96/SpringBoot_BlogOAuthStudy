package com.adam9e96.BlogStudy.config.jwt;

import com.adam9e96.BlogStudy.domain.User;
import com.adam9e96.BlogStudy.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());

        return Keys.hmacShaKeyFor(keyBytes);

    }

    // generateToken() 검증 테스트

    /**
     * <ul>
     *     <li> given : 토큰에 유저 정보를 추가하기 위해 테스트 유저를 만듭니다.</li>
     *     <li> when : 토큰 제공자의 generatedToken() 메서드를 호출해 토큰을 만듭니다.</li>
     *     <li> then : jjwt 라이브러리를 사용해 토큰을 복호화합니다. 토큰을 만들 때 클레임으로 넣어둔 id 값이 given 절에서 만든 유저
     *     id 와 동일한지 확인합니다.</li>
     * </ul>
     */
    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then
        Long userId = Jwts.parser()
                .verifyWith(getSecretKey()).build()
                .parseSignedClaims(token).getPayload()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    // validToken() 검증 테스트

    /**
     * <ul>
     *     <li> given : jjwt 라이브러리를 사용해 토큰을 생성합니다. 이때 만료 시가는 1970년 1월 1일부터 현재 시간을 밀리초 단위로
     *     치환한 값(new Date().getTime())에 1000을 빼, 이미 만료된 토큰으로 생성합니다.</li>
     *     <li> when : 토큰 제공자의 validToken() 메서드를 호출해 유효한 토큰인지 검증한 뒤 결괏값을 반환받습니다.</li>
     *     <li> then : 반환값이 false(요효한 토큰이 아님)인 것을 확인합니다.</li>
     * </ul>
     */
    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build().createToken(jwtProperties);

        // when
        boolean result = tokenProvider.validToken(token);

        // then
        assertThat(result).isFalse();
    }

    /**
     * <ul>
     *     <li> given : jjwt 라이브러리를 사용해 토큰을 생성합니다. 만료 시간은 현재 시간으로부터 14일 뒤로, 만료되지 않은
     *     토큰으로 생성합니다.</li>
     *     <li> when : 토큰 제공자의 validToken() 메서드를 호출해 유효한 토큰인지 검증한 뒤 결괏값을 반환받습니다.</li>
     *     <li> then : 반환값이 true(유효한 토큰임)인 것을 확인합니다.</li>
     * </ul>
     */
    @DisplayName("validToken(): 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);
        // when
        boolean result = tokenProvider.validToken(token);
        // then
        assertThat(result).isTrue();
    }

    // getAuthentication() 검증 테스트

    /**
     * <ul>
     *     <li> given : jjwt 라이브러리를 사용해 토큰을 생성합니다. 이때 토큰의 제목인 subject 는 "user@email.com"
     *     라는 값을 사용합니다</li>
     *     <li> when : 토큰 제공자의 getAuthentication() 메서드를 호출해 인증 객체를 반환받습니다.</li>
     *     <li> then : 반환받은 인증 객체의 유저 이름을 가져와 given 절에서 설정한 subject 값인
     *     "user@email.com" 과 같은지 확인합니다.</li>
     * </ul>
     */
    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).
                isEqualTo(userEmail);
    }

    // getUserId() 검증 테스트

    /**
     * <ul>
     *     <li> given : jjwt 라이브러리를 사용해 토큰을 생성합니다. 이때 클레임을 추가합니다.
     *     키는 "id", 값은 1이라는 유저 ID 입니다.</li>
     *     <li> when : 토큰 제공자의 getUserId() 메서드를 호출해 유저ID를 반환받습니다.</li>
     *     <li> then : 반환받은 유저 ID가 given 절에서 설정한 유저 ID값인 1과 같은지 확인합니다.</li>
     * </ul>
     */
    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when
        Long userIdByToken = tokenProvider.getUserId(token);

        // then
        assertThat(userIdByToken).isEqualTo(userId);
    }
}