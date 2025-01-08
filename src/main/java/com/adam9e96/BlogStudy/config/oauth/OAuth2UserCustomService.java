package com.adam9e96.BlogStudy.config.oauth;

import com.adam9e96.BlogStudy.domain.User;
import com.adam9e96.BlogStudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * {@code OAuth2UserCustomService}는 {@link DefaultOAuth2UserService}의 커스텀 구현체로서,
 * OAuth2 사용자 정보를 로드하고 처리하는 역할을 담당합니다.
 *
 * <p>
 * <strong>기능:</strong>
 * 이 서비스는 OAuth2 사용자 요청을 가로채어 OAuth2 제공자로부터 사용자 세부 정보를 가져오고,
 * 애플리케이션의 데이터베이스에 사용자 정보를 저장하거나 업데이트합니다.
 * 구체적으로, 제공된 이메일을 가진 사용자가 존재하는지 확인합니다:
 * </p>
 * <ul>
 *   <li>사용자가 존재하면, 사용자의 이름을 업데이트합니다.</li>
 *   <li>사용자가 존재하지 않으면, 제공된 이메일과 이름으로 새로운 사용자를 생성합니다.</li>
 * </ul>
 *
 * <p>
 * <strong>과거의 처리 방식:</strong>
 * 이전의 Spring Security 버전에서는 OAuth2 사용자 세부 정보를 수동으로 처리해야 했으며,
 * 이는 반복적이고 보일러플레이트 코드가 많았습니다.
 * {@link DefaultOAuth2UserService}의 도입으로 기본 구현이 제공되었지만,
 * 애플리케이션 특화된 사용자 처리를 위해서는 여전히 커스터마이징이 필요했습니다.
 * </p>
 *
 * <p>
 * 현재 {@code OAuth2UserCustomService}는 {@code DefaultOAuth2UserService}를 확장하여
 * 기본 동작을 활용하면서 사용자 지속성 로직을 추가합니다. 이러한 접근 방식은
 * 사용자 정보 로드와 비즈니스 특화된 사용자 관리를 분리함으로써 코드의 가독성과 재사용성을
 * 높여줍니다.
 * </p>
 *
 * @see DefaultOAuth2UserService
 * @see OAuth2User
 * @see OAuth2UserRequest
 * @see OAuth2AuthenticationException
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    /**
     * 제공된 {@link OAuth2UserRequest}를 기반으로 OAuth2 사용자를 로드합니다.
     *
     * <p>
     * 이 메서드는 기본 구현을 오버라이드하여 애플리케이션의 데이터베이스에
     * 사용자 정보를 저장하거나 업데이트하는 커스텀 로직을 포함합니다.
     * OAuth2 제공자로부터 사용자 속성을 가져와 이를 처리하고, 사용자 데이터가
     * 최신 상태인지 확인합니다.
     * </p>
     *
     * <p>
     * <strong>사용 예:</strong> 이 메서드는 OAuth2 인증 과정 중에 호출되어
     * OAuth2 사용자 정보를 애플리케이션의 사용자 관리 시스템과 통합합니다.
     * </p>
     *
     * @param userRequest 클라이언트 등록 정보와 액세스 토큰을 포함하는 OAuth2 사용자 요청
     * @return 인증된 사용자를 나타내는 {@link OAuth2User} 객체
     * @throws OAuth2AuthenticationException 인증 과정 중 오류가 발생한 경우
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 요청을 바탕으로 유저 정보를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest);
        log.info("OAuth2UserCustomService.looadUser 메소드 찾은값 {}", user.toString());
        // 데이터베이스에 사용자 정보 저장 또는 업데이트
        saveOrUpdate(user);
        // Spring Security에서 사용할 OAuth2User 반환
        return user;
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
    /**
     * 제공된 {@link OAuth2User}를 기반으로 새로운 사용자를 저장하거나 기존 사용자를 업데이트합니다.
     *
     * <p>
     * 이 메서드는 {@code OAuth2User}에서 사용자의 이메일과 이름을 추출합니다.
     * 그런 다음, 해당 이메일을 가진 사용자가 데이터베이스에 존재하는지 확인합니다:
     * </p>
     * <ul>
     *   <li>사용자가 존재하면, 해당 사용자의 이름을 업데이트합니다.</li>
     *   <li>사용자가 존재하지 않으면, 새로운 사용자 엔티티를 생성하여 저장합니다.</li>
     * </ul>
     *
     * <p>
     * <strong>과거의 처리 방식:</strong> 과거에는 사용자 데이터 동기화가 수동적이고
     * 여러 서비스 호출을 필요로 했으나, 이 메서드 내에 로직을 캡슐화하여
     * 더 간단하고 오류 가능성이 적은 방식으로 처리합니다.
     * </p>
     *
     * @param oAuth2User OAuth2 제공자로부터 가져온 사용자 속성을 포함하는 {@link OAuth2User} 객체
     * @return 저장되거나 업데이트된 {@link User} 엔티티
     */
    private User saveOrUpdate(OAuth2User oAuth2User) {
        // OAuth2User로부터 사용자 속성 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2UserCustomService.saveOrUpdate 메소드 찾은값 attributes : {}", attributes.toString());
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 이메일로 사용자를 조회하거나 존재하지 않으면 새 사용자 생성
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name)) // 기존 사용자의 이름 업데이트
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build()); // 이메일과 이름으로 새 사용자 생성
        log.info("OAuth2UserCustomService.saveOrUpdate 메소드 찾은값 user : {}", user.toString());

        // 사용자 저장 (신규 생성 또는 업데이트)
        return userRepository.save(user);
    }

}
