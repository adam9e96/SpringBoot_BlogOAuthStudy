package com.adam9e96.BlogStudy.config;

import com.adam9e96.BlogStudy.config.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * {@code TokenAuthenticationFilter}는 HTTP 요청마다 한 번씩 실행되는 커스텀 토큰 인증 필터입니다.
 *
 * <p>
 * <strong>기능:</strong>
 * 이 필터는 들어오는 HTTP 요청의 헤더에서 JWT 액세스 토큰을 추출하고, 토큰의 유효성을 검증한 후
 * 인증 정보를 {@link SecurityContextHolder}에 설정하여 Spring Security의 인증 메커니즘과 통합합니다.
 * </p>
 *
 * <p>
 * <strong>과거의 처리 방식:</strong>
 * 과거에는 토큰 기반 인증을 구현할 때 각 요청마다 수동으로 토큰을 추출하고 검증하는 로직을
 * 컨트롤러나 서비스 레이어에 직접 작성해야 했습니다. 이는 중복 코드와 보안 취약점의 원인이 될 수 있었습니다.
 * </p>
 *
 * <p>
 * <strong>현재 사용 방식:</strong>
 * 현재는 {@code OncePerRequestFilter}를 상속받아 필터를 구현함으로써 모든 요청에 대해 일관된
 * 토큰 인증 로직을 적용할 수 있습니다. 이를 통해 코드의 재사용성을 높이고, 보안성을 강화할 수 있습니다.
 * </p>
 *
 * @see OncePerRequestFilter
 * @see TokenProvider
 * @see SecurityContextHolder
 */
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    /**
     * 각 HTTP 요청에 대해 토큰을 추출하고 인증을 설정하는 메서드입니다.
     *
     * <p>
     * 이 메서드는 다음 단계를 수행합니다:
     * </p>
     * <ol>
     *   <li>요청 헤더에서 {@code Authorization} 키의 값을 조회합니다.</li>
     *   <li>헤더 값에서 {@code Bearer } 접두사를 제거하여 토큰을 추출합니다.</li>
     *   <li>추출한 토큰의 유효성을 {@link TokenProvider#validToken(String)} 메서드를 통해 검증합니다.</li>
     *   <li>토큰이 유효하면, {@link TokenProvider#getAuthentication(String)} 메서드를 호출하여 {@link Authentication} 객체를 생성합니다.</li>
     *   <li>생성된 {@link Authentication} 객체를 {@link SecurityContextHolder}에 설정하여 현재 스레드의 보안 컨텍스트에 인증 정보를 저장합니다.</li>
     *   <li>필터 체인을 통해 다음 필터로 요청을 전달합니다.</li>
     * </ol>
     *
     * @param request     현재 HTTP 요청
     * @param response    현재 HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 필터 처리 중 예외가 발생한 경우
     * @throws IOException      입출력 예외가 발생한 경우
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.info("TokenAuthenticationFilter 실행");
        log.info("Request URL: {}", request.getRequestURL());

        log.info("전체 Headers: {}", Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        headerName -> headerName,
                        request::getHeader
                )));

        // ==== 토큰 추출 ===== //
        // 요청 헤더의 Authorization 키의 값 조회
        // HTTP 요청 헤더에서 "Authorization" 값만 가져옴
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        log.info("Authorization Header: {}", authorizationHeader);  // 헤더 로그


        // 가져온 값에서 접두사 제거
        // 헤더 값에서 "Bearer " 접두사 제거하여 토큰 추출
        String token = getAccessToken(authorizationHeader);
        log.info("Extracted Token: {}", token);  // 추출된 토큰 로그

        // 가져온 토큰이 유효한지 확인하고, 유효하면 인증 정보 설정
        if (tokenProvider.validToken(token)) {
            // 유효한 토큰인 경우, 인증 정보 가져오기
            Authentication authentication = tokenProvider.getAuthentication(token);
            // 인증 정보를 SecurityContextHolder 에 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Valid Token. Authentication successful");
        } else {
            log.info("Invalid Token. Authentication failed");
        }
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * {@code Authorization} 헤더에서 토큰을 추출하는 유틸리티 메서드입니다.
     *
     * <p>
     * 이 메서드는 {@code Authorization} 헤더가 {@code Bearer } 접두사로 시작하는지 확인하고,
     * 그렇다면 접두사를 제거한 나머지 부분(즉, 실제 토큰)을 반환합니다.
     * </p>
     *
     * @param authorizationHeader {@code Authorization} 헤더의 값
     * @return 추출된 토큰 문자열 또는 {@code null} (헤더가 {@code Bearer }로 시작하지 않는 경우)
     */
    private String getAccessToken(String authorizationHeader) {
        // "Bearer " 접두사 제거 하여 실제 토큰만 추출
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}