package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.domain.entity.ProjectBookmark;
import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.domain.repository.*;
import com.f5.onepageresumebe.domain.repository.querydsl.UserQueryRepository;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailListResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public void addProjectBookmark(Integer projectId) {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        Project project = projectRepository.getById(projectId);

        ProjectBookmark projectBookmark = ProjectBookmark.create(user, project);

        projectBookmarkRepository.save(projectBookmark);
    }

    @Transactional
    public void deleteProjectBookmark(Integer projectId) {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        projectBookmarkRepository.deleteByUserIdAndProjectId(user.getId(), projectId);
    }

    public ProjectDetailListResponseDto getProjectBookmark() {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));

        List<Project> projects = new ArrayList<>();
        List<ProjectBookmark> projectBookmarkList = user.getProjectBookmarkList();
        for(ProjectBookmark projectBookmark : projectBookmarkList) {
            projects.add(projectBookmark.getProject());
        }

        return ProjectDetailListResponseDto.builder()
                .projects(ProjectUtil.projectToDetailResponseDtos(projects, projectImgRepository, projectStackRepository))
                .build();
    }
}