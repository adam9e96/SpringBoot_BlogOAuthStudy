package com.adam9e96.BlogStudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login() {
//        return "login";
        return "oauthLogin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
}
