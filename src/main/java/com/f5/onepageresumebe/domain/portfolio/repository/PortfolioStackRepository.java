package com.f5.onepageresumebe.domain.portfolio.repository;

import com.f5.onepageresumebe.domain.portfolio.entity.PortfolioStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioStackRepository extends JpaRepository<PortfolioStack, Integer> {

    @Modifying
    @Query("delete from PortfolioStack pf where pf.portfolio.id in :porfId")
    void deleteAllByPorfId(@Param("porfId") Integer porfId);


}
