package com.adam9e96.BlogStudy.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 블로그 게시물을 나타내는 엔티티.
 *
 * <p>
 * JPA를 사용하여 데이터베이스의 {@code articles} 테이블과 매핑됩니다.
 * 제목, 내용, 작성자, 생성 및 수정 타임스탬프를 포함합니다.
 *
 * <p>
 * <strong>주요 기능:</strong>
 * <ul>
 *   <li>게시물의 제목, 내용, 작성자 저장</li>
 *   <li>게시물의 고유 식별자(ID)를 자동 생성</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>주의 사항:</strong>
 *       기본 생성자는 PROTECTED 수준으로 제한되어 있어, 외부에서 직접 인스턴스를 생성할 수 없습니다.
 *       대신 {@link #builder()} 메서드를 사용하여 객체를 생성해야 합니다.
 * </p>
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 PROTECTED 로 설정
@EntityListeners(AuditingEntityListener.class) // 블로그 글 뷰 구현 (생성시간, 수정시간 관련)
@ToString
public class Article {

    /**
     * 게시물의 고유 식별자.
     * <p>
     * 데이터베이스의 기본 키로 사용되며, 자동으로 1씩 증가합니다.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키를 자동으로 1씩 증가
    @Column(name = "id", updatable = false)
    private Long id;

    /**
     * 게시물의 제목.
     */
    @Column(name = "title", nullable = false)
    private String title; // 게시물의 제목

    /**
     * 게시물의 내용.
     */
    @Column(name = "content", nullable = false)
    private String content; // 내용

    /**
     * 게시물 생성 시간.
     * <p>
     * 엔티티가 저장될 때 자동 설정됩니다.
     * </p>
     */
    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 게시물 수정 시간.
     * <p>
     * 엔티티가 수정될 떄 자동 업데이트 됩니다.
     * </p>
     */
    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 게시물 작성자.
     */
    @Column(name = "author", nullable = false)
    private String author;

    /**
     * 빌더 패턴을 사용한 Article 인스턴스 생성자.
     *
     * @param author  게시물의 작성자
     * @param title   게시물의 제목
     * @param content 게시물의 내용
     */
    @Builder
    public Article(String author, String title, String content) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    /**
     * 게시물의 제목과 내용을 업데이트.
     *
     * @param title   새로운 제목
     * @param content 새로운 내용
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
