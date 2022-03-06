package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.PortfolioStack;
import com.f5.onepageresumebe.domain.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioStackRepository extends JpaRepository<PortfolioStack, Integer> {

    Optional<PortfolioStack> findFirstByPortfolioAndStack(Portfolio portfolio, Stack stack);

    @Query("select pf.stack.name from PortfolioStack pf where pf.portfolio.id = :porfId")
    List<String> findStackNamesByPorfId(@Param("porfId") Integer porfId);

}
