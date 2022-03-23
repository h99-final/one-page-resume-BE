package com.f5.onepageresumebe.domain.git.repository.commit;

import com.f5.onepageresumebe.domain.git.entity.GitCommit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface GitCommitRepository extends JpaRepository<GitCommit, Integer> {

    @Query("select gc from GitCommit gc inner join gc.project p where p.id = :projectId")
    List<GitCommit> findAllByProjectId(@Param("projectId") Integer projectId);

    @Query("select gc from GitCommit gc inner join gc.project p where p.id = :projectId and gc.sha = :sha")
    Optional<GitCommit> findByShaAndProjectId(String sha,Integer projectId);

    @Modifying
    @Query("delete from GitCommit gc where gc.id = :commitId")
    void deleteById(@Param("commitId") Integer commitId);


}
