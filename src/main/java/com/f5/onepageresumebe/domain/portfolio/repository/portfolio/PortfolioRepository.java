package com.f5.onepageresumebe.domain.portfolio.repository.portfolio;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer>, PortfolioRepositoryCustom {

    @Modifying
    @Query("delete from Portfolio pf where pf.id = :id")
    void deleteById(@Param("id") Integer id);
}
