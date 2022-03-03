package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Integer> {

    @Query("select p.id from Project p where p.user.id = :userId")
    List<Integer> findProjectIdByUserId(@Param("userId") Integer userId);
}
