package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.ProjectImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectImgRepository extends JpaRepository<ProjectImg, Integer>  {

    Optional<ProjectImg> findFirstByProjectId(Integer projectId);

    @Query("select pi from ProjectImg pi where pi.project.id = :projectId")
    List<ProjectImg> findAllByProjectId(Integer projectId);
}
