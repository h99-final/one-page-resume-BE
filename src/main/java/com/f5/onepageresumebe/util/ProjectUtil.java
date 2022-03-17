package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.mysql.entity.Project;
import com.f5.onepageresumebe.domain.mysql.entity.ProjectImg;
import com.f5.onepageresumebe.domain.mysql.entity.User;
import com.f5.onepageresumebe.domain.mysql.repository.ProjectImgRepository;
import com.f5.onepageresumebe.domain.mysql.repository.ProjectStackRepository;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectUtil {

    public static List<ProjectResponseDto> projectToResponseDtos(List<Project> projects,
                                                                 ProjectImgRepository projectImgRepository,
                                                                 ProjectStackRepository projectStackRepository) {
        List<ProjectResponseDto> projectResponseDtos = new ArrayList<>();
        projects.forEach(project -> {
            ProjectImg projectImg = projectImgRepository.findFirstByProjectId(project.getId()).orElse(null);
            String projectImgUrl = null;
            if(projectImg!=null){
                projectImgUrl = projectImg.getImageUrl();
            }
            User user = project.getUser();

            ProjectResponseDto projectResponseDto = ProjectResponseDto.builder()
                    .id(project.getId())
                    .title(project.getTitle())
                    .content(project.getIntroduce())
                    .imageUrl(projectImgUrl)
                    .bookmarkCount(project.getBookmarkCount())
                    .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                    .userJob(user.getJob())
                    .username(user.getName())
                    .build();

            projectResponseDtos.add(projectResponseDto);

        });
        return projectResponseDtos;

    }

    public static Page<ProjectResponseDto> projectToResponseDtosPaging(Page<Project> projects,
                                                                       Pageable pageable,
                                                                       ProjectImgRepository projectImgRepository,
                                                                       ProjectStackRepository projectStackRepository) {
        List<ProjectResponseDto> projectResponseDtos = new ArrayList<>();
        projects.forEach(project -> {
            ProjectImg projectImg = projectImgRepository.findFirstByProjectId(project.getId()).orElse(null);
            String projectImgUrl = null;
            if(projectImg!=null){
                projectImgUrl = projectImg.getImageUrl();
            }
            User user = project.getUser();

            ProjectResponseDto projectResponseDto = ProjectResponseDto.builder()
                    .id(project.getId())
                    .title(project.getTitle())
                    .content(project.getIntroduce())
                    .imageUrl(projectImgUrl)
                    .bookmarkCount(project.getBookmarkCount())
                    .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                    .userJob(user.getJob())
                    .username(user.getName())
                    .build();

            projectResponseDtos.add(projectResponseDto);

        });
        return new PageImpl<>(projectResponseDtos,pageable,projectResponseDtos.size());

    }
  
    public static ProjectDetailResponseDto projectToDetailResponseDto(Project project,
                                                                      ProjectImgRepository projectImgRepository,
                                                                      ProjectStackRepository projectStackRepository) {

        List<ProjectImg> projectImgs = projectImgRepository.findAllByProjectId(project.getId());

            User user = project.getUser();

            ProjectDetailResponseDto projectDetailResponseDto = ProjectDetailResponseDto.builder()
                    .title(project.getTitle())
                    .content(project.getIntroduce())
                    .img(projectImgs.stream().map(ProjectImg::toProjectImgResponseDto).collect(Collectors.toList()))
                    .bookmarkCount(project.getBookmarkCount())
                    .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                    .userJob(user.getJob())
                    .username(user.getName())
                    .gitRepoUrl(project.getGitRepoUrl())
                    .build();

        return projectDetailResponseDto;
    }
}
