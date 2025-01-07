package com.adam9e96.BlogStudy.service;


import com.adam9e96.BlogStudy.domain.User;
import com.adam9e96.BlogStudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


/**
 * 사용자 정보를 제공하는 서비스 클래스.
 * <p>
 * 이 클래스는 {@link UserDetailsService} 인터페이스를 구현하여 스프링 시큐리티가
 * 사용자 인증 시 필요한 사용자 정보를 데이터베이스에서 로드할 수 있도록 합니다.
 * </p>
 *
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>사용자의 이메일을 기반으로 사용자 정보를 조회하여 {@link UserDetails} 형태로 반환합니다.</li>
 *   <li>사용자가 존재하지 않을 경우 {@link IllegalArgumentException}을 던집니다.</li>
 * </ul>
 * </p>
 *
 * <p>
 * 이 서비스는 주로 인증 프로세스 중에 사용되며, 스프링 시큐리티 설정에서
 * 사용자 인증을 위한 {@link UserDetailsService}로 등록됩니다.
 * </p>
 *
 * @see UserDetailsService
 * @see UserDetails
 * @see UserRepository
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 이름(이메일)을 기반으로 사용자 정보를 로드합니다.
     * <p>
     * 이 메서드는 스프링 시큐리티가 사용자를 인증할 때 호출됩니다.
     * 사용자의 이메일을 통해 데이터베이스에서 {@link User} 엔티티를 조회하고,
     * {@link UserDetails} 형태로 반환합니다.
     * </p>
     *
     * @param email 인증에 사용할 사용자의 이메일 주소.
     * @return {@link UserDetails} 형태의 사용자 정보.
     * @throws IllegalArgumentException 사용자를 찾을 수 없을 경우 발생.
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException((email + "에 해당하는 사용자가 없습니다.")));
    }
}
