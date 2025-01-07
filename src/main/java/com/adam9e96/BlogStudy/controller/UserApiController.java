package com.adam9e96.BlogStudy.controller;

import com.adam9e96.BlogStudy.dto.AddUserRequest;
import com.adam9e96.BlogStudy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 유저(회원) 관련 API 처리를 담당하는 컨트롤러
 * 회원가입, 로그아웃 등의 기능을 제공.
 */
@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    /**
     * 회원가입 요청을 처리하는 메서드.
     *
     * @param request 회원가입 폼에서 전달된 정보(이메일, 비밀번호)를 담은 DTO 객체
     * @return 회원 가입 완료 후 "/login" 페이지로 리다이렉트
     */
    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request); // 회원 가입 메서드 호출
        return "redirect:/login"; // 회원 가입이 완료된 이후에 로그인 페이지로 이동
    }

    /**
     * 로그아웃 요청을 처리하는 메서드
     *
     * @param request  현재 HTTP 요청 객체 (세션/쿠키 정보 등)
     * @param response 현재 HTTP 응답 객체
     * @return 로그아웃 후 "/login" 경로로 리다이렉트
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 스프링 시큐리티가 제공하는 SecurityContextLogoutHandler를 사용해 로그아웃 수행
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}