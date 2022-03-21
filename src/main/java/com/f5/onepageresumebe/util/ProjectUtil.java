package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.mysql.entity.Project;
import com.f5.onepageresumebe.domain.mysql.entity.ProjectImg;
import com.f5.onepageresumebe.domain.mysql.entity.User;
import com.f5.onepageresumebe.domain.mysql.repository.ProjectStackRepository;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import com.f5.onepageresumebe.domain.mysql.repository.querydsl.ProjectQueryRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectUtil {

    public static List<ProjectResponseDto> projectToResponseDtos(List<Project> projects,
                                                                  HashMap<Integer, ProjectImg> imageMap,
                                                                  HashMap<Integer, List<String>> stackMap) {

        List<ProjectResponseDto> projectResponseDtos = new ArrayList<>();
        projects.forEach(project -> {
            ProjectImg projectImg = imageMap.get(project.getId());
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
                    .stack(stackMap.get(project.getId()))
                    .userJob(user.getJob())
                    .username(user.getName())
                    .build();

            projectResponseDtos.add(projectResponseDto);

        });
        return projectResponseDtos;

    }

    public static Page<ProjectResponseDto> projectToResponseDtosPaging(Page<Project> projects,
                                                                       Pageable pageable,
                                                                       HashMap<Integer, ProjectImg> imageMap,
                                                                       HashMap<Integer, List<String>> stackMap) {
        List<ProjectResponseDto> projectResponseDtos = new ArrayList<>();
        projects.forEach(project -> {
            ProjectImg projectImg = imageMap.get(project.getId());
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
                    .stack(stackMap.get(project.getId()))
                    .userJob(user.getJob())
                    .username(user.getName())
                    .build();

            projectResponseDtos.add(projectResponseDto);

        });
        return new PageImpl<>(projectResponseDtos,pageable,projectResponseDtos.size());

    }
}
