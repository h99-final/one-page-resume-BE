package com.f5.onepageresumebe.domain.common.check;

import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepository;
import com.f5.onepageresumebe.domain.project.repository.project.ProjectRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckOwnerService {

    private final PortfolioRepository portfolioRepository;
    private final ProjectRepository projectRepository;

    public boolean isMyPorf(Integer porfId){

        try {
            //로그인 상태
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            //현재 로그인한 사람의 포트폴리오인지 확인
            return portfolioRepository.existsByUserEmailAndPorfId(userEmail, porfId);

        } catch (CustomAuthenticationException e) {
            //비로그인
            return false;
        }
    }

    public boolean isMyProject(Integer projectId){

        try {
            //로그인 상태
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            //현재 로그인한 사람의 포트폴리오인지 확인
            return projectRepository.findByUserEmailAndProjectId(userEmail, projectId).isPresent();

        } catch (CustomAuthenticationException e) {
            //비로그인
            return false;
        }
    }

}
