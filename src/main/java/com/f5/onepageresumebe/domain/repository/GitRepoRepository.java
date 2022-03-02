package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GitRepoRepository extends JpaRepository<GitRepository,Integer> {

    @Query("select gr from GitRepository gr join fetch gr.project p where p.id = :projectId and gr.name = :repoName")
    Optional<GitRepository> findByProjectIdAndName(@Param("projectId") Integer projectId,
                                                   @Param("repoName") String repoName);

    @Query("select gr from GitRepository gr where gr.name = :repoName")
    Optional<GitRepository> findByName(@Param("repoName") String repoName);
}
