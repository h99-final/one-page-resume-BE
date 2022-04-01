package com.f5.onepageresumebe.domain.project.repository;

import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectImgRepository extends JpaRepository<ProjectImg, Integer>  {

    Optional<ProjectImg> findFirstByProjectId(Integer projectId);

    @Query("select pi from ProjectImg pi inner join fetch pi.project p where p.id = :projectId")
    List<ProjectImg> findAllByProjectId(@Param("projectId") Integer projectId);

    @Modifying
    @Query("delete from ProjectImg pi where pi.id = :id")
    void deleteById(@Param("id") Integer id);
}
