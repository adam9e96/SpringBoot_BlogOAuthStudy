package com.adam9e96.BlogStudy.repository;

import com.adam9e96.BlogStudy.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 리프레시 토큰 데이터 접근을 담당하는 리포지토리 인터페이스.
 * <p>
 * 이 인터페이스는 {@link JpaRepository}를 확장하여 {@link RefreshToken} 엔티티에 대한
 * CRUD(생성, 조회, 업데이트, 삭제) 작업을 지원합니다. 또한, 특정 사용자 ID 또는 리프레시 토큰 값을
 * 기반으로 리프레시 토큰을 조회하는 커스텀 메서드를 제공합니다.
 * </p>
 *
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>{@link #findByUserId(Long)} - 사용자 ID를 기반으로 리프레시 토큰을 조회합니다.</li>
 *   <li>{@link #findByRefreshToken(String)} - 리프레시 토큰 값을 기반으로 리프레시 토큰을 조회합니다.</li>
 * </ul>
 * </p>
 *
 * <p>
 * 이 리포지토리는 주로 인증 및 권한 부여 과정에서 리프레시 토큰을 관리하는 데 사용됩니다.
 * </p>
 *
 * @see RefreshToken
 * @see JpaRepository
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 특정 사용자 ID에 해당하는 리프레시 토큰을 조회합니다.
     * <p>
     * 이 메서드는 주어진 {@code userId}와 연관된 {@link RefreshToken} 엔티티를
     * 데이터베이스에서 검색합니다. 사용자는 하나의 리프레시 토큰만을 가질 수 있도록
     * {@link RefreshToken} 엔티티에 {@code unique = true} 제약 조건이 설정되어 있습니다.
     * </p>
     *
     * @param userId 리프레시 토큰을 조회할 사용자의 고유 식별자.
     * @return 주어진 {@code userId}에 해당하는 {@link RefreshToken}을 포함하는 {@link Optional}.
     * 사용자가 존재하지 않거나 리프레시 토큰이 없는 경우 {@link Optional#empty()}를 반환합니다.
     */
    Optional<RefreshToken> findByUserId(Long userId);

    /**
     * 특정 리프레시 토큰 값을 기반으로 리프레시 토큰을 조회합니다.
     * <p>
     * 이 메서드는 주어진 {@code refreshToken} 값과 일치하는 {@link RefreshToken} 엔티티를
     * 데이터베이스에서 검색합니다. 이는 주로 클라이언트로부터 전달받은 리프레시 토큰의 유효성을
     * 확인하거나 갱신할 때 사용됩니다.
     * </p>
     *
     * @param refreshToken 조회할 리프레시 토큰 값.
     * @return 주어진 {@code refreshToken} 값에 해당하는 {@link RefreshToken}을 포함하는 {@link Optional}.
     * 리프레시 토큰이 존재하지 않을 경우 {@link Optional#empty()}를 반환합니다.
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
