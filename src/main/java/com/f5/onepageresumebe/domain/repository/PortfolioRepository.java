package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    @Query("select p from Portfolio p join fetch p.user u where u.email = :email")
    Optional<Portfolio> findByUserEmail(@Param("email") String email);

    @Query("select p from Portfolio p join fetch p.user where p.isTemp = false")
    List<Portfolio> findAllFetchUserIfPublic();

    @Query("select distinct pf.portfolio from PortfolioStack pf left join pf.portfolio p left join pf.stack s where s.name in :stackNames" +
            " and p.isTemp = false ")
    List<Portfolio> findAllByStackNamesIfPublic(@Param("stackNames") List<String> stackNames);
}
