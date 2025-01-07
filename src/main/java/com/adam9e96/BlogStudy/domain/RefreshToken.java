package com.adam9e96.BlogStudy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리프레시 토큰 엔티티
 * <p>
 * 이 클래스는 사용자별로 발급된 리프레시 토큰을 저장하고 관리합니다.<br>
 * 사용자가 새로운 액세스 토큰을 발급받을 때 사용되며, 액세스 토큰의 만료 후에도
 * 사용자가 재인증 없이 서비스를 계속 이용할 수 있도록 지원합니다.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

    /**
     * 리프레시 토큰의 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    /**
     * 리프레시 토큰과 연관된 사용자의 고유 식별자
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 실제 리프레시 토큰 값
     */
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    /**
     * 새로운 리프레시 토큰 인스턴스를 생성합니다.
     * @param userId 사용자의 고유 식별자
     * @param refreshToken 리프레시 토큰 값
     */
    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    /**
     * 기존 리프레시 토큰을 새로운 값으로 업데이트 합니다.
     */
    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}
