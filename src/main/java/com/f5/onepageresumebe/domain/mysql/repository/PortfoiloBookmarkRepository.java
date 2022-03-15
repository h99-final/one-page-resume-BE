package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfoiloBookmarkRepository extends JpaRepository<PortfoiloBookmark,Integer> {

    @Modifying
    @Query("delete from PortfoiloBookmark pb where pb.user.id = :userId and pb.portfolio.id = :portfoloId")
    void deleteByUserIdAndPortfolioId(@Param("userId") Integer userId, @Param("portfoloId") Integer portfoloId);

    Optional<PortfoiloBookmark> findFirstByUserIdAndPortfolioId(Integer userId, Integer portfoloId);


    List<PortfoiloBookmark> findAllByPortfolio(Portfolio portfolio);
}
