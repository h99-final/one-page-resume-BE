package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.GitFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GitFileRepository extends JpaRepository<GitFile,Integer> {

    @Modifying
    @Query("delete from GitFile gf where gf.id in :fileIds")
    void deleteAllByIds(@Param("fileIds") List<Integer> fileIds);

    @Query("select gf.id from GitFile gf join fetch gf.commit c where c.id = :commitId")
    List<Integer> findIdsByCommitId(@Param("commitId") Integer commitId);
}
