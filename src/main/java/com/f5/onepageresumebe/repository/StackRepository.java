package com.f5.onepageresumebe.repository;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StackRepository extends JpaRepository<Stack, Integer> {
     Integer findById(Portfolio portfolio);

 //    Optional <Stack> findByName(String Contents);
  //   List <String>  findByConetents(String contents);

     Stack findStackByName(String stackName);
}

