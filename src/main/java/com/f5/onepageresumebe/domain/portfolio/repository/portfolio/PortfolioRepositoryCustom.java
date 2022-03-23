package com.f5.onepageresumebe.domain.portfolio.repository.portfolio;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepositoryCustom {

    Optional<Portfolio> findByUserEmailFetchUser(String email);

    List<Portfolio> findAllByStackNamesIfPublicLimit(List<String> stacks);

    List<Portfolio> findAllFetchUserIfPublicLimit();

    List<String> findStackNamesByPorfId(Integer porfId);
}
