## 최초 OAuth2 로그인 흐름

```mermaid
sequenceDiagram
    actor User
    participant Client as Frontend
    participant VC as UserViewController
    participant Filter as OAuth2LoginAuthenticationFilter
    participant Provider as OAuth2AuthenticationProvider
    participant OAuth2 as Google OAuth2
    participant Service as OAuth2UserCustomService
    participant Handler as OAuth2SuccessHandler
    participant CookieRepo as AuthorizationRequestRepository
    participant TokenProvider as TokenProvider
    participant DB as Database
    User ->> Client: 접속
    Client ->> VC: GET /login
    VC -->> Client: oauthLogin.html 반환
    User ->> Client: Google 로그인 버튼 클릭
%% OAuth2 인증 시작
    Client ->> Filter: /oauth2/authorization/google
    Filter ->> OAuth2: 구글 로그인 페이지로 리다이렉트
    User ->> OAuth2: 구글 계정으로 로그인
    OAuth2 ->> Filter: 인증 코드 전달
%% 인증 처리
    Filter ->> Provider: OAuth2LoginAuthenticationToken 생성
    Provider ->> OAuth2: 인증 코드로 액세스 토큰 요청
    OAuth2 ->> Provider: 액세스 토큰 전달
    Provider ->> Service: OAuth2User 로드 요청
    Service ->> OAuth2: 사용자 정보 요청
    OAuth2 -->> Service: 사용자 정보 전달
    Service ->> DB: 사용자 정보 저장/갱신
    Service -->> Provider: OAuth2User 반환
    Provider -->> Filter: Authentication 객체 반환
%% 성공 처리
    Filter ->> Handler: 인증 성공 시 핸들러 호출
    Handler ->> CookieRepo: 인증 요청 정보 저장
    Handler ->> TokenProvider: JWT 토큰 생성 요청
    TokenProvider -->> Handler: Access Token & Refresh Token 반환
    Handler ->> DB: Refresh Token 저장
    Handler ->> Client: /articles?token=xxx로 리다이렉트
%% 최종 처리
    Client ->> Client: Access Token을 localStorage에 저장
    Client ->> VC: GET /articles
    VC -->> User: 게시글 목록 페이지

```

### 1. 초기 접근 및 OAuth2 인증 요청

1. 사용자가 `/login` 엔드포인트 접근
2. `UserViewController`가 OAuth2 로그인 페이지(`oauthLogin.html`) 반환
3. 사용자가 Google 로그인 버튼 클릭 시 `/oauth2/authorization/google` 엔드포인트로 리다이렉트
4. Spring Security의 `OAuth2LoginAuthenticationFilter`가 요청을 가로채서 Google 로그인 페이지로 리다이렉트

### 2. OAuth2 인증 처리

1. 사용자가 Google 계정으로 로그인
2. Google이 인증 코드를 전달
3. `OAuth2LoginAuthenticationFilter`가 인증 코드로 `OAuth2LoginAuthenticationToken` 생성
4. `OAuth2AuthenticationProvider`가 인증 코드를 Google OAuth2 서버에 전달하여 액세스 토큰 요청
5. 받은 액세스 토큰으로 사용자 정보 요청

### 3. 사용자 정보 처리

1. `OAuth2UserCustomService`가 Google에서 받은 사용자 정보(이메일, 이름 등)를 처리
2. 기존 사용자가 있다면 정보 업데이트, 없다면 새로 생성하여 DB에 저장
3. 처리된 사용자 정보를 `OAuth2User` 객체로 반환
4. `Provider`가 최종 `Authentication` 객체 생성

### 4. 인증 성공 처리

1. 인증 성공 시 `OAuth2SuccessHandler` 호출
2. 인증 요청 정보를 쿠키에 저장 (`OAuth2AuthorizationRequestBasedOnCookieRepository`)
3. `TokenProvider`를 통해 JWT 액세스 토큰과 리프레시 토큰 생성
4. 리프레시 토큰은 DB에 저장하고 쿠키에도 설정
5. 액세스 토큰을 URL 파라미터에 포함시켜 `/articles?token=xxx`로 리다이렉트

### 5. 클라이언트 최종 처리

1. 클라이언트의 `token.js`가 URL에서 액세스 토큰을 추출하여 localStorage에 저장
2. `/articles` 엔드포인트로 이동하여 게시글 목록 페이지 표시
