package com.adam9e96.BlogStudy.dto;

import com.adam9e96.BlogStudy.domain.Article;
import lombok.Getter;

/**
 * 뷰에게 데이터를 전달하기 위한 객체를 생성
 */
@Getter
public class ArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;


    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}



