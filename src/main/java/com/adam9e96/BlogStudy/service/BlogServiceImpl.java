package com.adam9e96.BlogStudy.service;

import com.adam9e96.BlogStudy.domain.Article;
import com.adam9e96.BlogStudy.dto.AddArticleRequest;
import com.adam9e96.BlogStudy.dto.UpdateArticleRequest;
import com.adam9e96.BlogStudy.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@link BlogService} 인터페이스의 구현체로, 블로그 게시물 관련 비즈니스 로직을 처리합니다.
 */
@RequiredArgsConstructor // final 이 붙거나 @NotNull 이 붙은 필드의 생성자 추가
@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    /**
     * 새로운 블로그 게시물을 저장합니다.
     *
     * <p>
     * AddArticleRequest 타입(DTO)으로 객체를 받으면
     * AddArticleRequest 의 toEntity 메서드를 이용해서 Article(entity) 객체로 변환하고
     * JPA의 CRUD 메서드인 save() 를 이용해 데이터베이스에 저장합니다.
     * </p>
     *
     * @param request 게시물 추가 요청 DTO
     * @param author  게시물 작성자
     * @return 저장된 {@link Article} 엔티티
     */
    @Override
    public Article save(AddArticleRequest request, String author) {
        return blogRepository.save(request.toEntity(author));
    }

    /**
     * 모든 블로그 게시물을 조회합니다.
     *
     * @return 모든 게시물의 리스트
     */
    @Override
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    /**
     * 특정 ID에 해당하는 블로그 게시물을 조회합니다.
     * <p>
     * JPA 지원 메서드인 findById()를 호출하고 orElseThrow()를 이용하여 값이 있으면
     * 해당 값을 반환하고 값이 없으면 지정한 예외와 메시지를 출력합니다.
     * </p>
     *
     * @param id 게시물 ID
     * @return 조회된 {@link Article} 엔티티
     * @throws IllegalArgumentException 해당 ID에 해당하는 게시물이 존재하지 않을 경우
     */
    @Override
    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("not found: " + id));
    }

    /**
     * 특정 ID에 해당하는 블로그 게시물을 삭제합니다.
     *
     * @param id 게시물 ID
     * @throws IllegalArgumentException 해당 ID에 해당하는 게시물이 존재하지 않거나 권한이 없을 경우
     */
    @Override
    public void delete(Long id) {
        Article article = blogRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    /**
     * 특정 ID에 해당하는 블로그 게시물을 수정합니다.
     *
     * @param id      게시물의 ID
     * @param request 게시물 수정 요청 DTO
     * @return 수정된 {@link Article} 엔티티
     * @throws IllegalArgumentException 게시물이 존재하지 않거나 권한이 없을 경우
     */
    @Override
    @Transactional
    public Article update(Long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    /**
     * 현재 인증된 사용자가 게시물의 작성자인지 확인합니다.
     *
     * @param article 검증할 게시물
     * @throws IllegalArgumentException 현재 사용자가 게시물의 작성자가 아닌 경우(권한이 없을 경우)
     */
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
