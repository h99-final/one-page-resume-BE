package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.domain.entity.ProjectImg;
import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.domain.repository.ProjectImgRepository;
import com.f5.onepageresumebe.domain.repository.ProjectStackRepository;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;

import java.util.ArrayList;
import java.util.List;

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
                    .userName(user.getName())
                    .build();

            projectResponseDtos.add(projectResponseDto);

        });
        return projectResponseDtos;
    }
    public static ProjectDetailResponseDto projectToDeatilResponseDto(Project project,
                                                                   ProjectImgRepository projectImgRepository,
                                                                   ProjectStackRepository projectStackRepository) {

        ProjectImg projectImg = projectImgRepository.findFirstByProjectId(project.getId()).orElse(null);
        String projectImgUrl = null;
        if (projectImg != null) {
            projectImgUrl = projectImg.getImageUrl();
        }
            User user = project.getUser();

            ProjectDetailResponseDto projectDetailResponseDto = ProjectDetailResponseDto.builder()
                    .title(project.getTitle())
                    .content(project.getIntroduce())
                    .imageUrl(projectImgUrl)
                    .bookmarkCount(project.getBookmarkCount())
                    .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                    .userJob(user.getJob())
                    .userName(user.getName())
                    .build();

        return projectDetailResponseDto;
    }
}
