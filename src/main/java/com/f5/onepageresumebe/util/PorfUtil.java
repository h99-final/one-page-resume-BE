package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.repository.PortfolioQueryRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PorfUtil {

    public static boolean isMyPorf(Integer porfId, PortfolioQueryRepository portfolioQueryRepository) {
        boolean res = false;
        try {
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                    new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
            if (portfolio.getId()==porfId) {
                res = true;
            }

        } catch (CustomAuthenticationException e) {
            return res;
        }
        return res;
    }

}
