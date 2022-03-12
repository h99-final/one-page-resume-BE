package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.ProjectStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectStackRepository extends JpaRepository<ProjectStack,Integer> {

    @Query("select s.name from ProjectStack ps inner join ps.stack s where ps.project.id = :projectId")
    List<String> findStackNamesByProjectId(@Param("projectId") Integer projectId);

    @Modifying
    @Query("delete from ProjectStack ps where ps.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") Integer projectId);
}
