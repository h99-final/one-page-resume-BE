package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.domain.entity.ProjectStack;
import com.f5.onepageresumebe.domain.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectStackRepository extends JpaRepository<ProjectStack,Integer> {

    Optional<ProjectStack> findFirstByProjectAndStack(Project project, Stack stack);

    @Query("select ps.stack.name from ProjectStack ps where ps.project.id = :projectId")
    List<String> findStackNamesByProjectId(@Param("projectId") Integer projectId);

}
