
package com.adam9e96.BlogStudy.dto;

import com.adam9e96.BlogStudy.domain.Article;
import lombok.Getter;

/**
 * 블로그 게시물 응답을 위한 데이터 전송 객체(DTO).
 *
 * <p>
 * 이 클래스는 {@link Article} 엔티티를 클라이언트에 전달할 응답 형식으로 변환하는 역할을 합니다.
 * 게시물의 제목과 내용을 포함합니다.
 * </p>
 *
 * @see Article
 */
@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    /**
     * {@link Article} 엔티티를 기반으로 {@code ArticleResponse} 객체를 생성합니다.
     *
     * @param article 변환할 {@link Article} 엔티티
     */
    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
