package com.adam9e96.BlogStudy.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 관련 설정을 담는 프로퍼티 클래스입니다.
 * <p>
 * 이 클래스는 {@code application.properties} 또는 {@code application.yml} 파일에서
 * 'jwt' 접두어가 있는 설정 값을 매핑하여 JWT 토큰의 발급자 및 비밀 키를 관리합니다.
 * </p>
 *
 * <p>
 * 예시 설정:
 * <pre>
 * jwt:
 *   issuer: your-issuer
 *   secretKey: your-secret-key
 * </pre>
 * </p>
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 토큰의 발급자(issuer)입니다.
     * <p>
     * 일반적으로 토큰을 발급한 서버의 식별자 역할을 하며, 토큰 검증 시 사용됩니다.
     * </p>
     *
     * <p>
     * 예시:
     * <pre>
     * jwt.issuer=your-issuer
     * </pre>
     * </p>
     */
    private String issuer;

    /**
     * JWT 토큰을 서명하는 데 사용되는 비밀 키(secret key)입니다.
     * <p>
     * 이 키는 토큰의 무결성과 신뢰성을 보장하는 데 사용되므로, 외부에 노출되지 않도록 안전하게 관리해야 합니다.
     * </p>
     *
     * <p>
     * 예시:
     * <pre>
     * jwt.secretKey=your-secret-key
     * </pre>
     * </p>
     */
    private String secretKey;
}
