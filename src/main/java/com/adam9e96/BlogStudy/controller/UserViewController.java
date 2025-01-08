package com.adam9e96.BlogStudy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 사용자 관련 뷰를 제공하는 컨트롤러.
 */
@Slf4j
@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login() {
        log.info("login() 호출됨 - 로그인 페이지로 이동");
//        return "login";
        return "oauthLogin";
    }

    @GetMapping("/signup")
    public String signup() {
        log.info("signup() 호출됨 - 회원가입 페이지로 이동");
        return "signup";
    }
}
