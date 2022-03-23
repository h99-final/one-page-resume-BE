package com.f5.onepageresumebe.domain.project.repository;

import com.f5.onepageresumebe.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Integer> {

    @Query("select p from Project p order by p.bookmarkCount desc")
    Page<Project> findAllByOrderByBookmarkCountDesc(Pageable pageable);

    @Query("select p.id from Project p where p.user.id = :userId")
    List<Integer> findProjectIdByUserId(@Param("userId") Integer userId);

//    @Query("select p from Project p inner join fetch p.user u inner join fetch u.portfolio where u.email = :userEmail")
//    List<Project> findAllByUserEmail(@Param("userEmail") String userEmail);

//    @Query("select p from Project p inner join fetch p.user u inner join fetch u.portfolio pf where p.id = :projectId and u.email = :userEmail")
//    Optional<Project> findByUserEmailAndProjectId(@Param("userEmail") String userEmail,
//                                               @Param("projectId") Integer projectId);

    @Query("select p from Project p where p.id in :projectIdList")
    List<Project> findAllByIds(@Param("projectIdList") List<Integer> projectIdList);

//    @Query("select distinct ps.project from ProjectStack ps left join ps.project left join ps.stack s where s.name in :stackNames")
//    List<Project> findAllByStackNames(@Param("stackNames") List<String> stackNames);

    @Query("select p from Project p where p.portfolio.id = :porfId")
    List<Project> findAllByPorfId(@Param("porfId") Integer porfId);

    @Modifying
    @Query("delete from Project p where p.id = :projectId")
    void deleteById(@Param("projectId") Integer projectId);
}