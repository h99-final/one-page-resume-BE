package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("select u from User u where u.email =:email")
    Optional<User> findByEmail(@Param("email") String email);
}