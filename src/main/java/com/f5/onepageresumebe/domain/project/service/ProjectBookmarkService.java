package com.f5.onepageresumebe.domain.project.service;

import com.f5.onepageresumebe.domain.project.entity.ProjectBookmark;
import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import com.f5.onepageresumebe.domain.user.repository.UserQueryRepository;
import com.f5.onepageresumebe.domain.portfolio.repository.PortfoiloBookmarkRepository;
import com.f5.onepageresumebe.domain.portfolio.repository.PortfolioRepository;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.project.repository.ProjectBookmarkRepository;
import com.f5.onepageresumebe.domain.project.repository.ProjectImgRepository;
import com.f5.onepageresumebe.domain.project.repository.ProjectRepository;
import com.f5.onepageresumebe.domain.project.repository.ProjectStackRepository;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.web.project.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProjectBookmarkService {

    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
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

    public List<ProjectDto.Response> getProjectBookmark() {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        List<Project> projects = new ArrayList<>();
        List<ProjectBookmark> projectBookmarkList = user.getProjectBookmarkList();
        for(ProjectBookmark projectBookmark : projectBookmarkList) {
            projects.add(projectBookmark.getProject());
        }
        HashMap<Integer, List<String>> stackMap = new HashMap<>();
        HashMap<Integer, ProjectImg> imageMap = new HashMap<>();

        for(Project project : projects) {
            Integer projectId = project.getId();

            stackMap.put(projectId,projectStackRepository.findStackNamesByProjectId(projectId));
            ProjectImg projectImg = projectImgRepository.findFirstByProjectId(projectId).orElse(null);
            if(projectImg != null) {
                imageMap.put(projectId, projectImg);}
            else {
                imageMap.put(projectId, null);
            }
        }

        return ProjectUtil.projectToResponseDtos(projects, imageMap, stackMap);

    }



//    @Transactional //포트폴리오 북마크 추가
//    public void addPortPolioBookmark(Integer portfoloId) {
//        String email = SecurityUtil.getCurrentLoginUserId();
//        User user = userQueryRepository.findByEmail(email).orElseThrow(() ->
//                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));
//
//        Portfolio portfolio = portfolioRepository.getById(portfoloId);
//        portfolio.updatePortPolioBookmarkCount(1);
//
//        PortfoiloBookmark portfoiloBookmark = PortfoiloBookmark.create(user, portfolio);
//
//        portfoiloBookmarkRepository.save(portfoiloBookmark);
//    }


//    @Transactional //포트폴리오 북마크 삭제
//    public void deletePortPolioBookmark(Integer portfoloId) {
//        String email = SecurityUtil.getCurrentLoginUserId();
//        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
//                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));
//        Portfolio portfolio = portfolioRepository.getById(portfoloId);
//        portfolio.updatePortPolioBookmarkCount(-1);
//
//        projectBookmarkRepository.deleteByUserIdAndProjectId(user.getId(), portfoloId);
//    }


//    public PorfDto.BookmarkResponse getPortPolioBookmark() { //포트폴리오 북마크 가져오기
//
//        String email = SecurityUtil.getCurrentLoginUserId();
//        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
//                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));
//
//        Portfolio portfolio = portfolioRepository.findById(user.getId()).get();
//
//        List<PortfoiloBookmark> portfoiloBookmarkList = portfoiloBookmarkRepository.findAllByPortfolio(portfolio);
//        List<Portfolio> portfolios = new ArrayList<>();
//
//        for (PortfoiloBookmark item: portfoiloBookmarkList){
//            portfolios.add(item.getPortfolio());
//        }
//
//        dto.setPortfolios(portfolios);
//        return  dto;
//    }
}