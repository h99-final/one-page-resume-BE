package com.f5.onepageresumebe.web.dto.project.requestDto;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class CreateProjectRequestDto {

    private String projectTitle;
    private String projectContent;
    private String gitRepoUrl;
    private String gitRepoName;
    private List<String> projectStack;
}
