package com.f5.onepageresumebe.domain.repository;


import com.f5.onepageresumebe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
