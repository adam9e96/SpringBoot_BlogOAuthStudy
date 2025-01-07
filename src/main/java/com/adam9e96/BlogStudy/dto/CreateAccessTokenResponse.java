package com.adam9e96.BlogStudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 새로 생성된 액세스 토큰을 클라이언트에게 반환하기 위한 DTO
 */
@AllArgsConstructor
@Getter
public class CreateAccessTokenResponse {
    private String accessToken;
}
