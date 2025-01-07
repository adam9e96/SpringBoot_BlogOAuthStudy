package com.adam9e96.BlogStudy.controller;

import com.adam9e96.BlogStudy.domain.Article;
import com.adam9e96.BlogStudy.dto.AddArticleRequest;
import com.adam9e96.BlogStudy.dto.UpdateArticleRequest;
import com.adam9e96.BlogStudy.repository.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest // 테스트용 애플리케이션 컨텍스트
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class BlogApiControllerTest {

    private static final Logger log = LoggerFactory.getLogger(BlogApiControllerTest.class);
    @Autowired
    protected MockMvc mockMvc;

    /**
     * 이 클래스로 만든 객체는 자바 객체를 JSON 데이터로 변환하는 직렬화 또는
     * 반대로 JSON 데이터를 자바에서 사용하기 위해 자바 객체로 변환하는 역직렬화를 할 때 사용합니다.
     */
    @Autowired
    protected ObjectMapper objectMapper; // 직렬화, 역직렬화를 위한 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        this.blogRepository.deleteAll();
    }

    // ============================================ //

    /**
     * <h2>CREATE</h2>
     * given : 블로그 글 추가에 필요한 요청 객체를 만듭니다.
     * when  : 블로그 글 추가 API에 요청을 보냅니다. 이떄 요청 타입은 JSON 이며, given 절에서 미리 만들어준 객체를 요청 본문으로
     * 함께 보냅니다.
     * then : 응답 코드가 201 Created 인지 확인합니다. Blog 를 전체 조회해 크기가 1인지 확인하고, 실제로 저장된 데이터와
     * 요청 값을 비교합니다.
     *
     * @throws Exception
     */
    @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
        log.info(userRequest.toString());
        // 자바객체를 JSON 으로 직렬화
        // writeValueAsString : Java 값을 문자열로 직렬화하는 데 사용할 수 있는 메서드
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        // 설정한 내용을 바탕으로 요청 전송
        ResultActions result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();
        assertThat(articles.size()).isEqualTo(1); // 크기가 1인지 검증. 블로그 글의 개수가 1인지 확인
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    /**
     * <h3>
     * 블로그 글 전체 조회 테스트
     * </h3>
     * <ul>
     *     <li> Given : 블로그 글을 저장합니다.</li>
     *     <li> When : 목록 조회 API를 호출합니다.</li>
     *     <li> Then : 응답 코드가 200 OK이고, 반환받은 값 중에 0번쨰 요소의 content와 title이 저장된 값과 같은지 확인합니다.</li>
     * </ul>
     */
    @DisplayName("findAllArticles() : 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "title9e96";
        final String content = "content9e96";

        // 블로그 글을 저장합니다.
        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when
        final ResultActions result = this.mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    /**
     * <h3>
     * 블로그 글을 id 로 조회하는 테스트
     * </h3>
     * <ul>
     *     <li> Given : 블로그 글을 저장합니다.</li>
     *     <li> When : 저장한 블로그 글을 id 값으로 APi를 호불합니다.</li>
     *     <li> Then : 응답 코드가 200 OK 이고, 반환받은 content 와 title이 저장된 값과 같은지 확인합니다.</li>
     * </ul>
     */
    @DisplayName("findArticle : 블로그 글을 id로 조회에 성공한다.")
    @Test
    public void findArticleById() throws Exception {

        // Given
        final String url2 = "/api/articles/{id}";
//        final String url = "/api/articles/1";
        final String title = "title9e96";
        final String content = "content9e96";

        Article article = Article.builder()
                .title(title)
                .content(content)
                .build();
        this.blogRepository.save(article);

        // When
        ResultActions result = this.mockMvc.perform(get(url2, article.getId())
                .contentType(MediaType.APPLICATION_JSON));


        // Then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    /**
     * <h3>
     * 블로그 글 id 삭제
     * </h3>
     * <ul>
     *     <li> Given : 블로그 글을 저장합니다. </li>
     *     <li> When : 저장할 블로그 글의 id 값으로 삭제 API를 호출합니다. </li>
     *     <li> Then : 응답코드가 200 OK이고, 블로그 글리스트 전체 조회해 조회한 배열 크기가 0인지 확인합니다.</li>
     * </ul>
     */
    @DisplayName("deleteArticle() : 블로그 글 삭제")
    @Test
    public void deleteArticle() throws Exception {
        // Given
        final String url = "/api/articles/{id}";
//        final String url = "/api/articles/1";
        final String title = "title9e96";
        final String content = "content9e96";
        Article saveArticle = blogRepository.save(
                Article.builder()
                        .title(title)
                        .content(content)
                        .build());

        // When
        mockMvc.perform(delete(url, saveArticle.getId()))
                .andExpect(status().isOk());
        // Then
        List<Article> articles = blogRepository.findAll();
        assertThat(articles).isEmpty();
    }

    /**
     * <h2>
     * 블로그 글 수정하기 id
     * </h2>
     * <ul>
     *     <li> Given : 블로그 글을 저장하고, 블로그 글 수정에 필요한 요청 객체를 만듭니다.</li>
     *     <li> WHEN :  UPDATE API로 수정 요청을 보냅니다. 이때 요청 타입은 JSON 이며, given 절에서 미리 만들어둔 객체를 요청 본문
     *     으로 함께 보냅니다.</li>
     *     <li> Then : 응답코드가 200 OK 인제 확인합니다. 블로그 글 id로 조회한 후에 값이 수정되었는지 확인합니다.</li>
     * </ul>
     */
    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        final String title = "title9e96";
        final String content = "content9e96";

        Article saveArticle = blogRepository.save(
                Article.builder()
                        .content(content)
                        .title(title)
                        .build());

        final String newTitle = "newTitle9e96";
        final String newContent = "newContent9e96";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        // when
        ResultActions result = mockMvc.perform(put(url, saveArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(saveArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);

    }
}