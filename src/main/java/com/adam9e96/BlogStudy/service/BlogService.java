package com.adam9e96.BlogStudy.service;

import com.adam9e96.BlogStudy.domain.Article;
import com.adam9e96.BlogStudy.dto.AddArticleRequest;
import com.adam9e96.BlogStudy.dto.UpdateArticleRequest;

import java.util.List;


/**
 * 블로그 게시물 관련 비즈니스 로직을 정의하는 서비스 인터페이스.
 */
public interface BlogService {
    /**
     * 새로운 블로그 게시물을 저장합니다.
     *
     * @param request 게시물 추가 요청 DTO
     * @param author  게시물 작성자
     * @return 저장된 게시물 엔티티
     */
    Article save(AddArticleRequest request, String author);

    /**
     * 모든 블로그 게시물을 조회합니다.
     *
     * @return 모든 게시물의 리스트
     */
    List<Article> findAll();

    /**
     * 특정 ID에 해당하는 블로그 게시물을 조회합니다.
     *
     * @param id 게시물의 ID
     * @return 조회된 게시물 엔티티
     */
    Article findById(Long id);

    /**
     * 특정 ID에 해당하는 블로그 게시물을 삭제합니다.
     *
     * @param id 게시물 ID
     */
    void delete(Long id);

    /**
     * 특정 ID에 해당하는 블로그 게시물을 수정합니다.
     *
     * @param id      게시물 ID
     * @param request 게시물 수정 요청 DTO
     * @return 수정된 게시물 엔티티
     */
    Article update(Long id, UpdateArticleRequest request);
}
