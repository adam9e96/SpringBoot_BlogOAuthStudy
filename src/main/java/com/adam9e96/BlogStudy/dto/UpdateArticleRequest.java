package com.adam9e96.BlogStudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 블로그 게시물 수정을 위한 요청 DTO.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class UpdateArticleRequest {
    private String title;
    private String content;
}
