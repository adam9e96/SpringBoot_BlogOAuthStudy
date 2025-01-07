package com.adam9e96.BlogStudy.repository;

import com.adam9e96.BlogStudy.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link Article} 엔티티를 관리하는 리포지토리 인터페이스.
 *
 * <p>
 * Spring Data JPA의 {@link JpaRepository}를 확장하여 CRUD 및 페이징, 정렬 기능을 제공합니다.
 * </p>
 *
 * @see JpaRepository
 * @see Article
 */
@Repository
public interface BlogRepository extends JpaRepository<Article, Long> {

}
