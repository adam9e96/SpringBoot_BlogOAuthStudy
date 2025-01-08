package com.adam9e96.BlogStudy.config;

import com.adam9e96.BlogStudy.config.jwt.TokenProvider;
import com.adam9e96.BlogStudy.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.adam9e96.BlogStudy.config.oauth.OAuth2SuccessHandler;
import com.adam9e96.BlogStudy.config.oauth.OAuth2UserCustomService;
import com.adam9e96.BlogStudy.repository.RefreshTokenRepository;
import com.adam9e96.BlogStudy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

/**
 * OAuth2 기반 인증(특히 구글 로그인을 위한)을 처리하기 위한 Spring Security 설정을 담당하는 클래스.
 * <p>
 * 기능
 * 1. OAAuth2 인증
 * 2. 토큰 관리 및 접근 제어
 * 3. 보안 설정
 */
@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    /**
     * 이 메서드는 WebSecurityCustomizer 빈을 정의 하여 Spring Security 가 특정 요청을 무시하도록 지시합니다.
     * 여기서 설정된 요청은 보안 필터 체인에서 제외됩니다.
     *
     * @return WebSecurityCustomizer 빈
     */
    @Bean
    public WebSecurityCustomizer configure() { // 스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console()) // H2 콘솔 접근 허용
                .requestMatchers( // 정적 리소스 접근 허용(img, css, js)
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")
                );
    }


    /**
     * 보안 필터 체인을 정의하여 HTTP 요청이 어떻게 보호되고 인증되는지 명시합니다.
     * 이 메서드는 SecurityFilterChain 빈을 정의합니다.
     * <p>
     * 1. 불필요한 기능 비활성화 (CSRF, HTTP Basic, Form Login, Logout)
     * 2. 세션 관리 (무상태 세션) -> 서버가 세션 상태를 유지하지 않도록 구성 (REST API 와 토큰 기반 인증을 사용하기 때문)
     * 3. 커스텀 토큰 인증 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 위치)
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF, HTTP Basic, Form Login, and Logout
                .csrf(AbstractHttpConfigurer::disable)
                // HTTP Basic 인증, 폼 로그인, 로그아웃:
                // OAuth2 기반 인증 및 토큰 관리를 중심으로 하기 위해 비활성화합니다
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                // Configure session management to be stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 커스텀 토큰 인증 필터 추가
                // UsernamePasswordAuthenticationFilter 필터 이전에 커스텀 TokenAuthenticationFilter 를 추가하여
                // JWT 토큰과 같은 토큰 기반 인증을 처리합니다.
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // 권한 부여 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token").permitAll() // /api/token 허용: 토큰 발급 엔드포인트에 대한 인증되지 않은 접근을 허용
                        .requestMatchers("/api/**").authenticated() // api/** 보호 : /api/** 경로에 대한 접근은 인증된 사용자만 허용
                        .anyRequest().permitAll() // 기타 요청 모두 허용(비 API 엔드포인트에 대한 모든 요청을 허용)
                )

                // OAuth2 로그인 구성
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 로그인 페이지 설정 (oauthLogin.html)에 리디렉션하여 OAuth2 로그인을 처리
                        // 커스텀 oAuthorizationRequestBasedOnCookieRepository 빈을 사용하여 OAuth2AuthorizationRequest 를
                        // 사용하여 쿠키에 저장하고 인증 요청을 관리합니다.
                        .authorizationEndpoint(authorization ->
                                authorization.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                        )
                        // 사용자 서비스 설정
                        // OAuth2 로그인 성공 시 사용자 정보를 가져오는 데 사용할 사용자 서비스를 설정합니다.
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuth2UserCustomService)
                        )
                        // 성공 핸들러 설정
                        .successHandler(oAuth2SuccessHandler())
                )

                // 로그아웃 성공 시 리디렉션할 URL을 설정합니다.
                .logout(logout ->
                        logout.logoutSuccessUrl("/login")
                )

                // API 엔드포인트에 대한 예외 처리
                .exceptionHandling(exception ->
                        exception.defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        )
                );
        return http.build();
    }


    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
