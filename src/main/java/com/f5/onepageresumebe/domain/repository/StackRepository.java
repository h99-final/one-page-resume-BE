package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StackRepository extends JpaRepository<Stack, Integer> {

    Optional<Stack> findFirstByName(String name);

    @Query("select s.name from Stack s where s.name in :names")
    List<String> findNamesByNamesIfExists(@Param("names") List<String> names);

}
