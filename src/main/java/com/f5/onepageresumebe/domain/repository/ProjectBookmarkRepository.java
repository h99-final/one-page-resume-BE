package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.ProjectBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Integer> {

    @Modifying
    @Query("delete from ProjectBookmark pb where pb.user.id = :userId and pb.project.id = :projectId")
    void deleteByUserIdAndProjectId(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

    Optional<ProjectBookmark> findFirstByUserIdAndProjectId(Integer userId, Integer projectId);
}