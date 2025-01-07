package com.adam9e96.BlogStudy.controller;

import com.adam9e96.BlogStudy.domain.Article;
import com.adam9e96.BlogStudy.dto.ArticleListViewResponse;
import com.adam9e96.BlogStudy.dto.ArticleViewResponse;
import com.adam9e96.BlogStudy.service.BlogServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 블로그 게시물 관련 뷰를 제공하는 컨트롤러
 *
 * <p>
 * 이 컨트롤러는 사용자에게 블로그 게시물 목록, 개별 게시물, 새로운 게시물작성/수정을 위한 뷰를 제공합니다.
 * </p>
 *
 * <p>
 * 주요 기능:
 * </p>
 * <ul>
 *     <li>게시물 목록 조회 및 표시</li>
 *     <li>특정 게시물 조회 및 표시</li>
 *     <li>새 게시물 작성 및 기존 게시물 수정</li>
 * </ul>
 *
 * @see BlogServiceImpl
 * @see Article
 * @see ArticleListViewResponse
 * @see ArticleViewResponse
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogServiceImpl blogService;

    /**
     * 모든 블로그 게시물을 조회하여 목록 뷰를 반환합니다.
     *
     * @param model 뷰에 데이터를 전달하기 위한 {@link Model} 객체
     * @return "articleList" 뷰 이름
     */
    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleListViewResponse::new)
                .toList();

        log.info("BlogViewController.getArticles 메소드 찾은값 {}", articles.toString());
        model.addAttribute("articles", articles); // 블로그 글 리스트 저장
        return "articleList"; // articleList.html 라는 뷰 조회
    }

    /**
     * 특정 ID에 해당하는 블로그 게시물을 조회하여 상세 뷰를 반환합니다.
     *
     * @param id    조회할 게시물의 ID
     * @param model 뷰에 데이터를 전달하기 위한 {@link Model} 객체
     * @return "article" 뷰 이름
     */
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable("id") Long id, Model model) {
        log.info("BlogViewController.getArticle 메소드 매개변수: id: {}", id);
        Article article = blogService.findById(id);
        log.info("BlogViewController.getArticle 메소드 article : {}", article.toString());
        model.addAttribute("article", new ArticleViewResponse(article));
        return "article";
    }

    /**
     * 새로운 게시물 작성 또는 기존 게시물 수정을 위한 뷰를 반환합니다.
     *
     * @param id    수정할 게시물의 ID (없을 경우 새로운 게시물 작성)
     * @param model 뷰에 데이터를 전달하기 위한 {@link Model} 객체
     * @return "newArticle" 뷰 이름
     */
    @GetMapping("/new-article")
    // id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있음)
    public String newArticle(@RequestParam(name = "id", required = false) Long id, Model model) {
        log.info("BlogViewController.newArticle 메소드 매개변수: id: {}", id);
        if (id == null) {// id 가 없으면 새로운 게시물 작성
            model.addAttribute("article", new ArticleViewResponse());
        } else { // id 가 없으면 기존 게시물 수정
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }

}
