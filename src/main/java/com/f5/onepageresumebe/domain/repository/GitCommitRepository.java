package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.GitCommit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface GitCommitRepository extends JpaRepository<GitCommit, Integer> {

    @Query("select gc from GitCommit gc inner join gc.project p where p.id = :projectId")
    List<GitCommit> findAllByProjectId(@Param("projectId") Integer projectId);

    GitCommit findBySha(String sha);

    @Modifying
    @Query("delete from GitCommit gc where gc.id = :commitId")
    void deleteById(@Param("commitId") Integer commitId);

//    @Query("select gc.id from GitCommit gc join fetch gc.repository r where r.id = :repoId")
//    List<Integer> findByRepoId(@Param("repoId") Integer repoId);
//
//    @Query("select gc from GitCommit gc join fetch gc.repository r where gc.sha = :sha and r.id = :repoId")
//    Optional<GitCommit> findByShaAndRepoId(@Param("sha") String sha,
//                                           @Param("repoId") Integer repoId);
}
