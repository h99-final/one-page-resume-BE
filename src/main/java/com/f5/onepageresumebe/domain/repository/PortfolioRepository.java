package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
}