package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.repository.PortfolioRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;

public class PorfUtil {

    public static boolean isMyPorf(Integer porfId, PortfolioRepository portfolioRepository) {
        boolean res = false;
        try {
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
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
