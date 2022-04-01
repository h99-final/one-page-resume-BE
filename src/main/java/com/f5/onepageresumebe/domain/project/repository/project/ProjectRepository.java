package com.f5.onepageresumebe.domain.project.repository.project;

import com.f5.onepageresumebe.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ProjectRepository extends JpaRepository<Project,Integer>, ProjectRepositoryCustom {

    @Query("select p from Project p order by p.bookmarkCount desc")
    Page<Project> findAllByOrderByBookmarkCountDescPaging(Pageable pageable);

    @Query("select p.id from Project p where p.user.id = :userId")
    List<Integer> findProjectIdByUserId(@Param("userId") Integer userId);

    @Query("select p.id from Project p inner join p.user u where u.email = :email")
    Set<Integer> findProjectIdsByUserEmail(@Param("email") String email);

    @Query("select p from Project p where p.id in :projectIdList")
    List<Project> findAllByIds(@Param("projectIdList") List<Integer> projectIdList);

    @Query("select p from Project p where p.portfolio.id = :porfId")
    List<Project> findAllByPorfId(@Param("porfId") Integer porfId);

    @Modifying
    @Query("delete from Project p where p.id = :projectId")
    void deleteById(@Param("projectId") Integer projectId);
}
