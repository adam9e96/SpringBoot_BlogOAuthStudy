package com.adam9e96;

import com.adam9e96.BlogStudy.domain.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectMapperTest {

    // ObjectMapper 생성
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("JSON을 Java 객체로 변환하기")
    void jsonToObject() throws IOException {
        // JSON 문자열
        String json = """
                {
                    "name": "John Doe",
                    "age": 30
                }
                """;

        // JSON 문자열을 객체로 역직렬화
        Member userFromString = objectMapper.readValue(json, Member.class);
        assertNotNull(userFromString);
        assertEquals("John Doe", userFromString.getName());
        assertEquals(30, userFromString.getAge());

        // 객체를 파일로 직렬화 후 파일에서 객체로 역직렬화
        File file = new File("user.json");
        objectMapper.writeValue(file, userFromString);
        assertTrue(file.exists());

        Member userFromFile = objectMapper.readValue(file, Member.class);
        assertNotNull(userFromFile);
        assertEquals("John Doe", userFromFile.getName());
        assertEquals(30, userFromFile.getAge());

        // 테스트 후 파일 삭제 (선택 사항)
        boolean deleted = file.delete();
        assertTrue(deleted, "파일 삭제에 실패했습니다.");
    }

    @Test
    @DisplayName("Java 객체를 JSON으로 변환하기")
    void objectToJson() throws IOException {
        // Java 객체 생성 (빌더 패턴 사용)
        Member user = Member.builder()
                .name("Jane Doe")
                .age(25)
                .build();

        // Java 객체를 JSON 문자열로 직렬화
        String userJson = objectMapper.writeValueAsString(user);
        assertNotNull(userJson);
        System.out.println("User as JSON: " + userJson);

        // JSON 문자열을 다시 객체로 변환하여 일치하는지 확인
        Member userFromJson = objectMapper.readValue(userJson, Member.class);
        assertNotNull(userFromJson);
        assertEquals("Jane Doe", userFromJson.getName());
        assertEquals(25, userFromJson.getAge());
    }
}
