package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.GitFile;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GitFileRepository extends JpaRepository<GitFile,Integer> {

    @Modifying
    @Query("delete from GitFile gf where gf.id = :gitFileId")
    void deleteById(@Param("gitFileId") Integer gitFileId);

//    @Modifying
//    @Query("delete from GitFile gf where gf.id in :fileIds")
//    void deleteAllByIds(@Param("fileIds") List<Integer> fileIds);
//
//    @Query("select gf.id from GitFile gf join fetch gf.commit c where c.id = :commitId")
//    List<Integer> findIdsByCommitId(@Param("commitId") Integer commitId);
}
