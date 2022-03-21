package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.ProjectImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectImgRepository extends JpaRepository<ProjectImg, Integer>  {

    Optional<ProjectImg> findFirstByProjectId(Integer projectId);

    @Query("select pi from ProjectImg pi inner join fetch pi.project p where p.id = :projectId")
    List<ProjectImg> findAllByProjectId(Integer projectId);

    @Modifying
    @Query("delete from ProjectImg pi where pi.id = :id")
    void deleteById(Integer id);
}
