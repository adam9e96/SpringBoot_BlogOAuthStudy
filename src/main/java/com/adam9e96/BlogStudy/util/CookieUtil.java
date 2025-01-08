package com.adam9e96.BlogStudy.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

/**
 * 쿠키 관련 유틸리티 메서드를 제공하는 클래스입니다.
 */
@Slf4j
public class CookieUtil {

    /**
     * 지정된 이름과 값을 가지는 쿠키를 생성하여 응답에 추가합니다.
     *
     * @param response 응답 객체로, 쿠키를 추가할 대상입니다.
     * @param name     생성할 쿠키의 이름입니다.
     * @param value    생성할 쿠키의 값입니다.
     * @param maxAge   쿠키의 유효 기간(초 단위)입니다.
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        log.info("addCookie()에서 생성된 쿠키 객체 :{}", cookie);
        response.addCookie(cookie);
    }

    /**
     * 요청에서 지정된 이름의 쿠키를 찾아 삭제합니다.
     * <p>
     * 삭제하는 방법은 없으므로 파라미터로 넘어온 키의 쿠키를 빈 값으로 바꾸고 만료 시간을 0으로 설정해
     * 쿠키가 재생성 되자마자 만료 처리합니다.
     *
     * @param request  요청 객체로, 삭제할 쿠키를 포함하고 있습니다.
     * @param response 응답 객체로, 쿠키 삭제를 위한 설정을 추가합니다.
     * @param name     삭제할 쿠키의 이름입니다.
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) { // 수정: cookie.getName()과 name을 비교
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            log.info("deleteCookie()에서 삭제된 쿠키 객체 :{}", cookie);
        }

    }

    /**
     * 객체를 직렬화하여 쿠키에 저장할 수 있는 문자열로 변환합니다.
     *
     * @param obj 직렬화할 객체입니다.
     * @return 직렬화된 객체를 Base64 URL 인코딩한 문자열입니다.
     */
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    /**
     * 쿠키의 값을 역직렬화하여 객체로 변환합니다.
     *
     * @param cookie 쿠키 객체로, 값을 역직렬화할 데이터가 포함되어 있습니다.
     * @param cls    변환할 객체의 클래스 타입입니다.
     * @param <T>    변환할 객체의 타입입니다.
     * @return 역직렬화된 객체입니다.
     * @throws IllegalArgumentException 변환할 수 없는 경우 발생할 수 있습니다.
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
