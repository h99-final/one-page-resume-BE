package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.domain.entity.ProjectImg;
import com.f5.onepageresumebe.domain.repository.ProjectImgRepository;
import com.f5.onepageresumebe.domain.repository.ProjectStackRepository;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailResponseDto;

import java.util.ArrayList;
import java.util.List;

public class ProjectUtil {
    public static List<ProjectDetailResponseDto> projectToDetailResponseDtos(List<Project> projects,
                                                                      ProjectImgRepository projectImgRepository,
                                                                      ProjectStackRepository projectStackRepository) {
        List<ProjectDetailResponseDto> projectDetailResponseDtos = new ArrayList<>();
        projects.forEach(project -> {
            ProjectImg projectImg = projectImgRepository.findFirstByProjectId(project.getId()).orElse(null);
            String projectImgUrl = null;
            if(projectImg!=null){
                projectImgUrl = projectImg.getImageUrl();
            }

            ProjectDetailResponseDto projectDetailResponseDto = ProjectDetailResponseDto.builder()
                    .title(project.getTitle())
                    .content(project.getIntroduce())
                    .imgUrl(projectImgUrl)
                    .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                    .build();

            projectDetailResponseDtos.add(projectDetailResponseDto);

        });
        return projectDetailResponseDtos;
    }
}
