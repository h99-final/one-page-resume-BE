package com.f5.onepageresumebe.domain.project.repository;

import com.f5.onepageresumebe.domain.project.entity.ProjectBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;


public interface ProjectBookmarkRepository extends JpaRepository<ProjectBookmark, Integer> {

    @Query("select pb.project.id from ProjectBookmark pb inner join pb.user u where u.email = :email")
    Set<Integer> findByUserEmail(@Param("email") String email);

    @Modifying
    @Query("delete from ProjectBookmark pb where pb.user.id = :userId and pb.project.id = :projectId")
    void deleteByUserIdAndProjectId(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

    @Modifying
    @Query("delete from ProjectBookmark pb where pb.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Integer projectId);
    Optional<ProjectBookmark> findFirstByUserIdAndProjectId(Integer userId, Integer projectId);
}