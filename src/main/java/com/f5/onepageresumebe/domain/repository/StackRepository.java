package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StackRepository extends JpaRepository<Stack, Integer> {
    Stack findStackByName(String name);
}
