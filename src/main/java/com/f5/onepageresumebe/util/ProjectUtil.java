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
                    .title(project.getTitle())
                    .content(project.getIntroduce())
                    .bookmarkCount(project.getBookmarkCount())
                    .imageUrl(projectImgUrl)
                    .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                    .userJob(user.getJob())
                    .username(user.getName())
                    .build();

            projectResponseDtos.add(projectResponseDto);

        });
        return projectResponseDtos;
    }
}
