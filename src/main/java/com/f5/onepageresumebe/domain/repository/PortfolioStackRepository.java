package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.PortfolioStack;
import com.f5.onepageresumebe.domain.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioStackRepository extends JpaRepository<PortfolioStack, Integer> {


    @Query("select s.name from PortfolioStack pf inner join pf.stack s where pf.portfolio.id = :porfId")
    List<String> findStackNamesByPorfId(@Param("porfId") Integer porfId);

    @Modifying
    @Query("delete from PortfolioStack pf where pf.portfolio.id in :porfId")
    void deleteAllByPorfId(@Param("porfId") Integer porfId);
}
