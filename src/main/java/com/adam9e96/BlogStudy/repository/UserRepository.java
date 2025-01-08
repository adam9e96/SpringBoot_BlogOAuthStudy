package com.adam9e96.BlogStudy.repository;

import com.adam9e96.BlogStudy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


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
