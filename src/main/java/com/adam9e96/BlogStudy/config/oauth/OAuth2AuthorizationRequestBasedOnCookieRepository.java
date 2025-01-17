package com.adam9e96.BlogStudy.config.oauth;

import com.adam9e96.BlogStudy.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import java.util.Objects;


/**
 * OAuth2 인증 요청을 쿠키에 저장하고 관리하는 리포지토리 클래스입니다.
 *
 * <p>
 * 이 클래스는 OAuth2 인증 과정에서 사용되는 인증 요청 객체를 HTTP 쿠키에 저장하고,
 * 필요할 때 이를 조회하거나 삭제하는 기능을 제공합니다. 이는 상태를 유지하지 않는(stateless)
 * HTTP 프로토콜에서 OAuth2 인증 흐름을 올바르게 처리하기 위해 필요합니다.
 * </p>
 *
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>인증 요청 객체를 쿠키에 저장</li>
 *   <li>쿠키에서 인증 요청 객체 로드</li>
 *   <li>인증 요청 관련 쿠키 삭제</li>
 * </ul>
 * </p>
 */
@Slf4j
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    /**
     * OAuth2 인증 요청 객체를 저장할 때 사용할 쿠키의 이름
     */
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    /**
     * OAuth2 인증 요청 객체의 쿠키 만료 시간(초)
     * 18000초 = 5시간
     */
    private final static int COOKIE_EXPIRE_SECONDS = 18000;

    /**
     * HTTP 요청에서 인증 요청 객체를 제거합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 제거된 인증 요청 객체
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    /**
     * HTTP 요청의 쿠키에서 인증 요청 객체를 로드합니다.
     *
     * @param request 현재 HTTP 요청
     * @return 쿠키에서 역직렬화된 OAuth2AuthorizationRequest 객체
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        log.info("loadAuthorizationRequest()에서 가져온 쿠키 객체 :{}", cookie);

        return CookieUtil.deserialize(Objects.requireNonNull(cookie), OAuth2AuthorizationRequest.class);
    }

    /**
     * 인증 요청 객체를 HTTP 응답의 쿠키에 저장합니다.
     *
     * <p>
     * 만약 인증 요청 객체가 null인 경우, 관련된 쿠키를 모두 제거합니다.
     * 그렇지 않은 경우, 인증 요청 객체를 직렬화하여 쿠키에 저장합니다.
     * </p>
     *
     * @param authorizationRequest 저장할 인증 요청 객체
     * @param request              현재 HTTP 요청
     * @param response             현재 HTTP 응답
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }
        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);

    }

    /**
     * 인증 요청과 관련된 모든 쿠키를 제거합니다.
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}
