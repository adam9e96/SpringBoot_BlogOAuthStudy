
package com.adam9e96.BlogStudy.dto;

import com.adam9e96.BlogStudy.domain.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 블로그 게시물 상세 정보를 클라이언트에 전달하기 위한 데이터 전송 객체(DTO).
 *
 * <p>
 * 이 클래스는 {@link Article} 엔티티를 클라이언트가 이해하기 쉬운 형태로 변환하여 제공합니다.
 * 게시물의 ID, 제목, 내용, 작성일자, 작성자를 포함합니다.
 * </p>
 *
 * @see Article
 */
@NoArgsConstructor
@Getter
public class ArticleViewResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;

    /**
     * {@link Article} 엔티티를 기반으로 {@code ArticleViewResponse} 객체를 생성합니다.
     *
     * @param article 변환할 {@link Article} 엔티티
     */
    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.author = article.getAuthor();
    }
}
