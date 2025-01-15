package com.adam9e96.BlogStudy.controller;

import com.adam9e96.BlogStudy.domain.Article;
import com.adam9e96.BlogStudy.dto.AddArticleRequest;
import com.adam9e96.BlogStudy.dto.ArticleResponse;
import com.adam9e96.BlogStudy.dto.UpdateArticleRequest;
import com.adam9e96.BlogStudy.service.BlogServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 블로그 게시물 관련 RESTful API를 제공하는 컨트롤러.
 *
 * <p>
 * 이 컨트롤러는 클라이언트의 HTTP 요청을 처리하고,
 * {@link BlogServiceImpl}를 통해 비즈니스 로직을 수행한 후,
 * 적절한 HTTP 응답을 반환합니다.
 * </p>
 *
 * <p>
 * <strong>주요 기능:</strong>
 * <ul>
 *   <li>새로운 게시물 추가</li>
 *   <li>모든 게시물 조회</li>
 *   <li>ID로 게시물 조회</li>
 *   <li>ID로 게시물 삭제</li>
 *   <li>ID로 게시물 수정</li>
 * </ul>
 * </p>
 *
 * @see UpdateArticleRequest
 * @see AddArticleRequest
 * @see BlogServiceImpl
 * @see Article
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogServiceImpl blogService;

    /**
     * 새로운 블로그 게시물을 추가합니다.
     * <p>
     * 클라이언트로부터 {@link AddArticleRequest} 객체를 JSON 형식으로 전달받아,
     * {@link Article} 엔티티로 변환한 후 데이터베이스에 저장합니다.
     * 성공적으로 저장되면 저장된 게시물 정보를 포함하여 HTTP 201 (Created) 상태 코드를 반환합니다.
     * </p>
     *
     * @param request   게시물 추가 요청 데이터.
     * @param principal 현재 인증된 사용자의 정보
     * @return {@link Article} 엔티티와 HTTP 상태 코드를 반환(201 Created).
     * @throws IllegalArgumentException 요청 데이터가 유효하지 않은 경우
     * <p>
     * <strong>응답 코드:</strong>
     * </p>
     *
     * <ul>
     * <li>{@code 201 Created}: 게시물이 성공적으로 생성되었을 때 반환됩니다.</li>
     * <li>{@code 400 Bad Request}: 요청 데이터가 유효하지 않을 경우 반환될 수 있습니다.</li>
     * <li>{@code 500 Internal Server Error}: 서버 내부 오류가 발생했을 때 반환될 수 있습니다.</li>
     * </ul>
     */
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {
        Article savedArticle = blogService.save(request, principal.getName());

        log.info("BlogApiController.addArticle 메소드 매개변수:" +
                " request: {}," +
                " principal: {}", request.toString(), principal.getName());

        /*
         * 응답 코드 설명:
         *
         * - HttpStatus.CREATED (201):
         *   요청이 성공적으로 수행되었으며, 새로운 리소스(게시물)가 생성되었음을 나타냅니다.
         *   클라이언트는 이 응답을 통해 새로 생성된 리소스의 URI를 알 수 있습니다.
         *
         * - HttpStatus.BAD_REQUEST (400):
         *   클라이언트가 잘못된 데이터를 전송했을 경우 반환됩니다.
         *   예를 들어, 필수 필드가 누락되었거나 데이터 형식이 잘못된 경우입니다.
         *
         * - HttpStatus.INTERNAL_SERVER_ERROR (500):
         *   서버 내부에서 예기치 않은 오류가 발생했을 때 반환됩니다.
         *   이는 주로 코드의 버그나 외부 시스템과의 통신 오류 등으로 인해 발생할 수 있습니다.
         */
        log.info("BlogApiController.addArticle 메소드 반환값: {}", savedArticle.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    /**
     * 모든 블로그 게시물을 조회합니다.
     *
     * @return 모든 게시물의 리스트와 HTTP 상태 코드 200 (OK)를 반환합니다.
     *
     * <p>
     * /api/articles 로 GET 요청이 오면 글 목록을 조회할 findAllArticles()를 실행합니다. <br>
     * 내부족으로는 글 전체를 조회하는 findAll() 메서드를 호출한 다음 <br>
     * 응답용 객체인 ArticleResponse 타입으로 파싱해 body 에 담아서 클라이언트에 전송합니다.(반환)
     * </p>
     */
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        log.info("BlogApiController.findAllArticles 메소드 반환값: {}", blogService.findAll().toString());

        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.ok().body(articles);
    }

    /**
     * 특정 ID에 해당하는 블로그 게시물을 조회합니다.
     *
     * @param id 조회할 게시물의 ID
     * @return 조회된 게시물 정보와 HTTP 상태 코드 200 (OK)를 반환합니다.
     */
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable("id") Long id) {
        log.info("BlogApiController.findArticle 메소드 매개변수: id: {}", id);
        Article article = blogService.findById(id);

        log.info("BlogApiController.findArticle 메소드 반환값: {}", article.toString());
        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    /**
     * 특정 ID에 해당하는 블로그 게시물을 삭제합니다.
     *
     * @param id 삭제할 게시물의 ID
     * @return HTTP 상태 코드 200 (OK)를 반환합니다.
     * @throws IllegalArgumentException 해당 ID에 해당하는 게시물이 존재하지 않거나 권한이 없는경우
     */
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable("id") Long id) {
        log.info("BlogApiController.deleteArticle 메소드 매개변수: id: {}", id);
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 ID에 해당하는 블로그 게시물을 수정합니다.
     *
     * @param id      수정할 게시물의 ID
     * @param request 수정할 게시물의 요청 DTO
     * @return 수정된 게시물 정보와 HTTP 상태 코드 200 (OK)를 반환합니다.
     * @throws IllegalArgumentException 해당 ID에 해당하는 게시물이 존재하지 않거나 권한이 없는 경우
     */
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable("id") Long id, @RequestBody UpdateArticleRequest request) {
        log.info("BlogApiController.updateArticle 메소드 매개변수: id: {}, request: {}", id, request.toString());
        Article updatedArticle = blogService.update(id, request);

        log.info("BlogApiController.updateArticle 메소드 반환값: {}", updatedArticle.toString());
        return ResponseEntity.ok().body(updatedArticle);
    }
}