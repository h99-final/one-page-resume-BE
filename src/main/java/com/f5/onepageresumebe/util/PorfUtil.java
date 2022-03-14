package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.mysql.entity.*;
import com.f5.onepageresumebe.domain.mysql.repository.PortfolioStackRepository;
import com.f5.onepageresumebe.domain.mysql.repository.ProjectImgRepository;
import com.f5.onepageresumebe.domain.mysql.repository.ProjectStackRepository;
import com.f5.onepageresumebe.domain.mysql.repository.querydsl.PortfolioQueryRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;

import java.util.ArrayList;
import java.util.List;

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
