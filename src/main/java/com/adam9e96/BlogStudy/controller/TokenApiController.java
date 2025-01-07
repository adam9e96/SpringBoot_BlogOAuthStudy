package com.adam9e96.BlogStudy.controller;

import com.adam9e96.BlogStudy.dto.CreateAccessTokenRequest;
import com.adam9e96.BlogStudy.dto.CreateAccessTokenResponse;
import com.adam9e96.BlogStudy.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    /**
     * TokenService 의 createNewAccessToken 메소드를 호출하여 요청에서 추출한 리프래시 토큰을 기반으로 새로운 액세스 토큰을 생성합니다.
     *
     * @param request 요청
     * @return HTTP 응답용 객체 CreateAccessTokenResponse
     */
    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
    (@RequestBody CreateAccessTokenRequest request) {

        // 새로운 액세스 토큰 생성
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        // 응답 생성
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
