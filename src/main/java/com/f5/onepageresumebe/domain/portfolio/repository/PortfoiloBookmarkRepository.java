package com.f5.onepageresumebe.domain.portfolio.repository;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.entity.PortfolioBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfoiloBookmarkRepository extends JpaRepository<PortfolioBookmark,Integer> {

    @Modifying
    @Query("delete from PortfolioBookmark pb where pb.user.id = :userId and pb.portfolio.id = :portfolioId")
    void deleteByUserIdAndPortfolioId(@Param("userId") Integer userId, @Param("portfolioId") Integer portfolioId);

    Optional<PortfolioBookmark> findFirstByUserIdAndPortfolioId(Integer userId, Integer portfolioId);


    List<PortfolioBookmark> findAllByPortfolio(Portfolio portfolio);
}
