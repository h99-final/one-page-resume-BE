package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.web.project.dto.ProjectDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectUtil {

    public static List<ProjectDto.Response> projectToResponseDtos(List<Project> projects,
                                                           HashMap<Integer, ProjectImg> imageMap,
                                                           HashMap<Integer, List<String>> stackMap) {

        List<ProjectDto.Response> projectResponseDtos = new ArrayList<>();
        projects.forEach(project -> {
            ProjectImg projectImg = imageMap.get(project.getId());
            String projectImgUrl = null;
            if(projectImg!=null){
                projectImgUrl = projectImg.getImageUrl();
            }
            User user = project.getUser();

            ProjectDto.Response projectResponseDto = ProjectDto.Response.builder()
                    .id(project.getId())
                    .title(project.getTitle())
                    .content(project.getIntroduce())
                    .imageUrl(projectImgUrl)
                    .bookmarkCount(project.getBookmarkCount())
                    .stack(stackMap.get(project.getId()))
                    .userJob(user.getJob())
                    .username(user.getName())
                    .build();

            projectResponseDtos.add(projectResponseDto);

        });
        return projectResponseDtos;

    }

}
