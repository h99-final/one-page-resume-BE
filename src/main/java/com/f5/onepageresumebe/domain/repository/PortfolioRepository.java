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
    Optional<Portfolio> findByUserEmailFetchUser(@Param("email") String email);

    @Query("select p from Portfolio p join fetch p.user where p.isTemp = false")
    List<Portfolio> findAllFetchUserIfPublic();

    @Query("select distinct p from PortfolioStack pf left join pf.portfolio p left join fetch p.user left join pf.stack s where s.name in :stackNames" +
            " and p.isTemp = false")
    List<Portfolio> findAllByStackNamesIfPublic(@Param("stackNames") List<String> stackNames);

    @Query("select c.id from Career c where c.portfolio.id = :porfId")
    List<Integer> findCareerIdByPorfId(@Param("porfId") Integer porfId);
}
