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

/**
 * {@code TokenApiController}는 클라이언트의 요청을 받아 새로운 액세스 토큰을 생성하는 REST API 엔드포인트를 제공합니다.
 *
 * <p>
 * <strong>기능:</strong>
 * 이 컨트롤러는 클라이언트로부터 리프레시 토큰을 포함한 요청을 받아 {@link TokenService}를 통해
 * 새로운 액세스 토큰을 생성하고, 이를 응답으로 반환합니다.
 * </p>
 *
 * <p>
 * <strong>과거의 처리 방식:</strong>
 * 과거에는 액세스 토큰의 갱신 로직이 분산되어 관리되었거나, 별도의 엔드포인트 없이 인증 과정에서
 * 직접 처리되는 경우가 많았습니다. 이는 보안 및 유지보수 측면에서 비효율적일 수 있었습니다.
 * </p>
 *
 * <p>
 * <strong>현재 사용 방식:</strong>
 * 현재는 {@code TokenApiController}를 통해 액세스 토큰 갱신 로직을 중앙화하여 관리함으로써
 * 보안성을 높이고, 코드의 재사용성과 유지보수성을 향상시킵니다. 또한, 명확한 API 구조를 통해
 * 클라이언트와의 통신을 간소화합니다.
 * </p>
 *
 * @see TokenService
 * @see CreateAccessTokenRequest
 * @see CreateAccessTokenResponse
 */
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
