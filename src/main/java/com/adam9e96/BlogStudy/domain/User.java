package com.adam9e96.BlogStudy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 사용자 정보를 담당하는 도메인 클래스
 * <p>
 * 이 클래스는 애플리케이션의 사용자 정보를 저장하며 {@link UserDetails} 인터페이스를 구현하여
 * 스프링 시큐리티와 통합됩니다.
 * 이를 통해 인증 및 권한 부여 과정을 관리합니다.
 * </p>
 * <p>
 * OAuth2 서비스 구현하기
 */
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails { // UserDetails 를 상속받아 인증 객체로 사용

    /**
     * 사용자의 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    /**
     * 사용자의 이메일 주소, 로그인 시 사용자명으로 사용됩니다.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * 사용자의 암호화된 비밀번호
     * <p>
     * 스크링 시큐리티에서 인증 시 사용됩니다.
     * </p>
     */
    @Column(name = "password")
    private String password;

    // OAuth1 서비스 구현하기
    // 사용자 이름
    @Column(name = "nickname", unique = true)
    private String nickname;

    /**
     * 사용자 인스턴스를 생성하는 빌더 패턴을 사용한 생성자
     *
     * @param email    사용자의 이메일 주소.
     * @param password 사용자의 암호화된 비밀번호.
     * @param nickname 리로스 서버에 제공해주는 이름.
     */
    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    // 사용자 이름 변경
    public User update(String nickname) {
        this.nickname = nickname;

        return this;
    }

    /**
     * 스프링 시큐리티는 사용자 권한을 문자열로 관리하지 않고 GrantedAuthority 객체로 관리합니다.
     * 현재는 단일 권한 "user"만 부여 하고있습니다.
     *
     * <p>
     * 사용자의 권한을 반환합니다.
     * </p>
     *
     * @return 사용자의 권한을 나타내는 {@link GrantedAuthority} 컬렉션.
     */
    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }


    // ===== 인증에 필요한 메서드 : getPassword(), getUsername() =====

    /**
     * 사용자의 패스워드를 반환합니다.
     *
     * @return 사용자의 암호화된 비밀번호.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 사용자의 id(이메일)을 사용자명으로 반환합니다.
     *
     * @return 사용자의 이메일 주소.
     */
    @Override
    public String getUsername() {
        return email;
    }

    // ===== 계정 상태 관련 메서드 =====
    // 계정의 만료, 잠금, 비밀번호 만료, 사용 가능 여부를 반환합니다.
    // isAccountNonExpired(),
    // isAccountNonLocked(),
    // isCredentialsNonExpired(),
    // isEnabled()
    // =====      END            ====

    // 현재는 모두 true로 설정하여 인증에 별다른 제약을 두지 않았습니다.

    /**
     * 계정의 만료 여부를 반환합니다.
     *
     * @return {@code true}이면 계정이 만료되지 않았음을 나타냅니다.
     */
    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않음
    }

    /**
     * 계정의 잠금 여부를 반환합니다.
     *
     * @return {@code true}이면 계정이 잠금되지 않았음을 나타냅니다.
     */
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 잠기지 않음
    }


    /**
     * 자격 증명의 만료 여부를 반환합니다.
     *
     * @return {@code true}이면 자격 증명이 만료되지 않았음을 나타냅니다.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않음
    }

    /**
     * 계정의 사용 가능 여부를 반환합니다.
     *
     * @return {@code true}이면 계정을 사용할 수 있음을 나타냅니다.
     */
    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true -> 사용 가능
    }


}
