package com.f5.onepageresumebe.domain.portfolio.repository.portfolio;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepositoryCustom {

    Optional<Portfolio> findByUserEmailFetchUser(String email);

    List<Portfolio> findAllByStackNamesIfPublicPaging(List<String> stacks, Pageable pageable);

    List<Portfolio> findAllFetchUserIfPublicPaging(Pageable pageable);

    List<String> findStackNamesByPorfId(Integer porfId);

    boolean existsByUserEmailAndPorfId(String userEmail, Integer porfId);
}
