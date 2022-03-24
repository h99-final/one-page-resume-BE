package com.f5.onepageresumebe.domain.user.repository;

import com.f5.onepageresumebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer>,UserRepositoryCustom {

    User findUserByNameAndPhoneNum(String name, String phoneNum);

    boolean existsByEmail(String email);
}