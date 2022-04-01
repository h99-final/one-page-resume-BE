package com.f5.onepageresumebe.domain.stack.repository;

import com.f5.onepageresumebe.domain.stack.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StackRepository extends JpaRepository<Stack, Integer> {

    Optional<Stack> findFirstByName(String name);

}
