package com.f5.onepageresumebe.domain.git.repository.file;

import com.f5.onepageresumebe.domain.git.entity.GitFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GitFileRepository extends JpaRepository<GitFile,Integer>, GitFileRepositoryCustom{

    @Modifying
    @Query("delete from GitFile gf where gf.id = :gitFileId")
    void deleteById(@Param("gitFileId") Integer gitFileId);

}
