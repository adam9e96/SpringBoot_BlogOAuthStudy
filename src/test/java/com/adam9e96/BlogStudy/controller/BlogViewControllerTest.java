package com.adam9e96.BlogStudy.controller;

import com.adam9e96.BlogStudy.service.BlogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class BlogViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BlogServiceImpl blogService;

    /**
     * MockMvc 객체를 생성합니다.
     * 이 메서드는 각 테스트 메서드가 실행되기 전에 실행됩니다.
     */
    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }


}