package com.f5.onepageresumebe.domain.portfolio.repository.portfolio;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer>, PortfolioRepositoryCustom {


}
