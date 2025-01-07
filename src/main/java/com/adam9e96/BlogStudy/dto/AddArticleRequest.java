package com.adam9e96.BlogStudy.dto;

import com.adam9e96.BlogStudy.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 새로운 블로그 게시물 추가 요청을 하는 DTO.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class AddArticleRequest {
    private String title;
    private String content;

    /**
     * 블로그 글을 추가할때 저장할 엔티티로 변환하는 용도로 사용합니다.
     * <p>
     * DTO 를 {@link Article} 엔티티로 변환합니다.
     *
     * @param author 게시물 작성자
     * @return 변환된 {@link Article} 엔티티
     */
    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
