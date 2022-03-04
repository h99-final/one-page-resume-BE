package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    @Query("select p from Portfolio p join fetch p.user u where u.email = :email")
    Optional<Portfolio> findByUserEmail(@Param("email") String email);
}
