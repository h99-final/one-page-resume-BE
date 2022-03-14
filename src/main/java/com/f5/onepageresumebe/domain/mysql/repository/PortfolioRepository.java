package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

//    @Query("select p from Portfolio p join fetch p.user u where u.email = :email")
//    Optional<Portfolio> findByUserEmailFetchUser(@Param("email") String email);

//    @Query("select p from Portfolio p join fetch p.user where p.isTemp = false")
//    List<Portfolio> findAllFetchUserIfPublic();

//    @Query("select distinct p from PortfolioStack pf left join pf.portfolio p left join fetch p.user left join pf.stack s where s.name in :stackNames" +
//            " and p.isTemp = false")
//    List<Portfolio> findAllByStackNamesIfPublic(@Param("stackNames") List<String> stackNames);

}
