package com.f5.onepageresumebe.domain.mysql.service;

import com.f5.onepageresumebe.domain.mysql.entity.*;
import com.f5.onepageresumebe.domain.mysql.repository.*;
import com.f5.onepageresumebe.domain.mysql.repository.querydsl.UserQueryRepository;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.PorfUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfResponseDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfreadResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookmarkService {

    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final UserRepository userRepository;
    private final ProjectImgRepository projectImgRepository;
    private final ProjectStackRepository projectStackRepository;
    private final UserQueryRepository userQueryRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfoiloBookmarkRepository portfoiloBookmarkRepository;

    @Transactional
    public void addProjectBookmark(Integer projectId) {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        Project project = projectRepository.getById(projectId);
        project.updateBookmarkCount(1);

        ProjectBookmark projectBookmark = ProjectBookmark.create(user, project);

        projectBookmarkRepository.save(projectBookmark);
    }

    @Transactional
    public void deleteProjectBookmark(Integer projectId) {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));
        Project project = projectRepository.getById(projectId);
        project.updateBookmarkCount(-1);

        projectBookmarkRepository.deleteByUserIdAndProjectId(user.getId(), projectId);
    }

    public List<ProjectResponseDto> getProjectBookmark() {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        List<Project> projects = new ArrayList<>();
        List<ProjectBookmark> projectBookmarkList = user.getProjectBookmarkList();
        for(ProjectBookmark projectBookmark : projectBookmarkList) {
            projects.add(projectBookmark.getProject());
        }

        return ProjectUtil.projectToResponseDtos(projects, projectImgRepository, projectStackRepository);

    }



    @Transactional //포트폴리오 북마크 추가
    public void addPortPolioBookmark(Integer portfoloId) {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        Portfolio portfolio = portfolioRepository.getById(portfoloId);
        portfolio.updatePortPolioBookmarkCount(1);

        PortfoiloBookmark portfoiloBookmark = PortfoiloBookmark.create(user, portfolio);

        portfoiloBookmarkRepository.save(portfoiloBookmark);
    }


    @Transactional //포트폴리오 북마크 삭제
    public void deletePortPolioBookmark(Integer portfoloId) {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));
        Portfolio portfolio = portfolioRepository.getById(portfoloId);
        portfolio.updatePortPolioBookmarkCount(-1);

        projectBookmarkRepository.deleteByUserIdAndProjectId(user.getId(), portfoloId);
    }


    public PorfreadResponseDto getPortPolioBookmark() { //포트폴리오 북마크 가져오기

        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        PorfreadResponseDto dto = new PorfreadResponseDto();
        Portfolio portfolio = portfolioRepository.findById(user.getId()).get();

        List<PortfoiloBookmark> portfoiloBookmarkList = portfoiloBookmarkRepository.findAllByPortfolio(portfolio);
        List<Portfolio> portfolios = new ArrayList<>();

        for (PortfoiloBookmark item: portfoiloBookmarkList){
                portfolios.add(item.getPortfolio());
        }



        dto.setPortfolios(portfolios);
        return  dto;


    }
}