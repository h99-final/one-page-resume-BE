package com.f5.onepageresumebe.domain.portfolio.repository;

import com.f5.onepageresumebe.domain.portfolio.entity.PortfoiloBookmark;
import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfoiloBookmarkRepository extends JpaRepository<PortfoiloBookmark,Integer> {

    @Modifying
    @Query("delete from PortfoiloBookmark pb where pb.user.id = :userId and pb.portfolio.id = :portfolioId")
    void deleteByUserIdAndPortfolioId(@Param("userId") Integer userId, @Param("portfolioId") Integer portfolioId);

    Optional<PortfoiloBookmark> findFirstByUserIdAndPortfolioId(Integer userId, Integer portfolioId);


    List<PortfoiloBookmark> findAllByPortfolio(Portfolio portfolio);
}
