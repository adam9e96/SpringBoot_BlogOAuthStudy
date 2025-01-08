package com.adam9e96.BlogStudy.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 추가 요청을 위한 데이터 전송 객체(DTO).
 */
@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
