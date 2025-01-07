package com.adam9e96.BlogStudy.repository;

import com.adam9e96.BlogStudy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <p>{@code UserRepository}는 데이터베이스에서 {@link User} 엔티티와 상호 작용하기 위한
 * Spring Data JPA 인터페이스입니다. <br>
 * {@link JpaRepository}를 상속하여, 기본적인 CRUD(Create, Read, Update, Delete) 기능을
 * 비롯해, 페이징, 정렬 등에 대한 메서드를 자동으로 제공합니다.</p>
 *
 * <p>이 외에도, 사용자 정의 메서드를 통해 이메일을 기준으로 사용자 정보를 조회할 수 있습니다.</p>
 *
 * @author adam9e96
 * @see User
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 주어진 이메일 주소를 통해 사용자를 검색합니다.
     *
     * <p>이메일로 검색된 사용자가 존재하면 해당 {@link User}를 담은 {@link Optional}을 반환하고,
     * 존재하지 않을 경우 빈 {@code Optional}을 반환합니다.</p>
     *
     * @param email 조회할 사용자의 이메일
     * @return 해당 이메일로 조회된 {@link User}를 감싼 {@code Optional}, 없으면 비어있음
     */
    Optional<User> findByEmail(String email);
}
