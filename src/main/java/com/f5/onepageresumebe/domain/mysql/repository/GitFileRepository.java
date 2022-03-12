package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.GitFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitFileRepository extends JpaRepository<GitFile,Integer> {

//    @Modifying
//    @Query("delete from GitFile gf where gf.id in :fileIds")
//    void deleteAllByIds(@Param("fileIds") List<Integer> fileIds);
//
//    @Query("select gf.id from GitFile gf join fetch gf.commit c where c.id = :commitId")
//    List<Integer> findIdsByCommitId(@Param("commitId") Integer commitId);
}
