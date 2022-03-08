package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Career;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Integer> {

    @Query("select c from Career c where c.portfolio.id = :porfId")
    List<Career> findAllByPorfId(@Param("porfId") Integer porfId);

    @Modifying
    @Query("delete from Career c where c.portfolio.id = :porfId")
    void deleteAllByPorfId(@Param("porfId") Integer porfId);
}
