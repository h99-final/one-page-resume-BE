package com.f5.onepageresumebe.domain.user.repository;

import com.f5.onepageresumebe.domain.user.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<User> findByEmail(String email);
}
