package com.f5.onepageresumebe.domain.user.repository;

import com.f5.onepageresumebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Integer>,UserRepositoryCustom {

    User findUserByNameAndPhoneNum(String name, String phoneNum);

    boolean existsByEmail(String email);

    @Modifying
    @Query("delete from User u where u.id = :id")
    void deleteById(@Param("id") Integer userId);
}