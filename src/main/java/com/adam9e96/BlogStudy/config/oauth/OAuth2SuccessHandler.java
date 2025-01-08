package com.adam9e96.BlogStudy.config.oauth;

import com.adam9e96.BlogStudy.config.jwt.TokenProvider;
import com.adam9e96.BlogStudy.domain.RefreshToken;
import com.adam9e96.BlogStudy.domain.User;
import com.adam9e96.BlogStudy.repository.RefreshTokenRepository;
import com.adam9e96.BlogStudy.service.UserService;
import com.adam9e96.BlogStudy.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

/**
 * {@code OAuth2SuccessHandler}는 OAuth2 인증이 성공했을 때 호출되는 핸들러로,
 * 리프레시 토큰과 액세스 토큰을 생성하여 사용자에게 전달하고, 인증 관련 속성을 정리한 후
 * 지정된 URL로 리다이렉트합니다.
 *
 * <p>
 * <strong>기능:</strong>
 * 이 핸들러는 OAuth2 인증이 성공적으로 완료된 후 다음과 같은 작업을 수행합니다:
 * </p>
 * <ol>
 *   <li>인증된 OAuth2 사용자의 정보를 로깅합니다.</li>
 *   <li>리프레시 토큰을 생성하고 데이터베이스에 저장한 후, 쿠키에 설정합니다.</li>
 *   <li>액세스 토큰을 생성하여 클라이언트에 전달할 URL에 추가합니다.</li>
 *   <li>인증 관련 속성을 정리하고, 불필요한 쿠키를 제거합니다.</li>
 *   <li>최종적으로 클라이언트를 지정된 URL로 리다이렉트합니다.</li>
 * </ol>
 *
 * <p>
 * <strong>과거의 처리 방식:</strong>
 * 과거에는 OAuth2 인증 성공 시, 토큰 생성과 저장, 쿠키 설정, 리다이렉트 등의 로직이
 * 분산되어 관리되었거나, 컨트롤러 내에서 직접 처리되었습니다. 이는 코드의 중복과
 * 유지보수의 어려움을 초래할 수 있었습니다.
 * </p>
 *
 * <p>
 * <strong>현재 사용 방식:</strong>
 * 현재는 {@code SimpleUrlAuthenticationSuccessHandler}를 확장하여 인증 성공 시
 * 수행해야 할 일들을 하나의 핸들러에 집중시킴으로써, 코드의 재사용성과 유지보수성을
 * 향상시켰습니다. 또한, 토큰 관리와 쿠키 설정 로직을 별도의 유틸리티 클래스로
 * 분리하여 책임을 명확히 했습니다.
 * </p>
 *
 * @see SimpleUrlAuthenticationSuccessHandler
 * @see TokenProvider
 * @see UserService
 * @see RefreshTokenRepository
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;
//    private final AuthorizationRequestRepository authorizationRequestRepository;

    /**
     * OAuth2 인증이 성공했을 때 호출되는 메서드입니다.
     *
     * <p>
     * 이 메서드는 다음 단계를 수행합니다:
     * </p>
     * <ol>
     *   <li>인증된 {@link OAuth2User}의 정보를 로깅합니다.</li>
     *   <li>사용자의 이메일을 기반으로 {@link User} 엔티티를 조회합니다.</li>
     *   <li>리프레시 토큰을 생성하고 데이터베이스에 저장한 후, 쿠키에 추가합니다.</li>
     *   <li>액세스 토큰을 생성하여 리다이렉트 URL에 포함시킵니다.</li>
     *   <li>인증 관련 속성을 정리하고, 불필요한 쿠키를 제거합니다.</li>
     *   <li>클라이언트를 지정된 URL로 리다이렉트합니다.</li>
     * </ol>
     *
     * @param request        현재 HTTP 요청
     * @param response       현재 HTTP 응답
     * @param authentication 인증된 사용자 정보
     * @throws IOException 입출력 예외가 발생한 경우
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth2User: {}", oAuth2User.getAttributes().toString());
//        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        log.info("99", oAuth2User.getAttributes().get("email").toString());
        log.info("999User: {}", user.toString());


        // 1. 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);
        // 2. 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);
        // 3. 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);
        // 4. 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 생성된리프레시 토큰을 전달받아 데이터베이스에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
//        authorizationRequestRepository.removeAuthorizationRequest(request, response);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);


    }

    //' 액세스 토큰을 패스에 추가
    /**
     * 액세스 토큰을 포함한 리다이렉트 URL을 생성합니다.
     *
     * <p>
     * 이 메서드는 지정된 리다이렉트 경로에 액세스 토큰을 쿼리 파라미터로 추가하여 최종 URL을 생성합니다.
     * </p>
     *
     * @param token 생성된 액세스 토큰
     * @return 액세스 토큰이 포함된 최종 리다이렉트 URL
     */
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

}
