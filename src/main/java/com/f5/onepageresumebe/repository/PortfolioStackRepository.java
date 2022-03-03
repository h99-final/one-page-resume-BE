package com.f5.onepageresumebe.repository;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.PortfolioStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioStackRepository extends JpaRepository<PortfolioStack, Integer> {
}
