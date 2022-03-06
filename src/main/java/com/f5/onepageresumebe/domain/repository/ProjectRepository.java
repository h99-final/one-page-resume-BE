package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Integer> {

    @Query("select p.id from Project p where p.user.id = :userId")
    List<Integer> findProjectIdByUserId(@Param("userId") Integer userId);

    @Query("select p from Project p inner join fetch p.user u where u.email = :userEmail")
    List<Project> findAllByUserEmail(@Param("userEmail") String userEmail);

    @Query("select p from Project p where p.id in :projectIdList")
    List<Project> findAllByIds(@Param("projectIdList") List<Integer> projectIdList);

    @Query("select distinct ps.project from ProjectStack ps left join ps.project left join ps.stack s where s.name in :stackNames")
    List<Project> findAllByStackNames(@Param("stackNames") List<String> stackNames);

    @Query("select p from Project p where p.portfolio.id = :porfId")
    List<Project> findAllByPorfId(@Param("porfId") Integer porfId);
}
