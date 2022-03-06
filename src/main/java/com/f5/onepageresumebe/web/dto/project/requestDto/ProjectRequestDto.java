package com.f5.onepageresumebe.web.dto.project.requestDto;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class ProjectRequestDto {

    private String title;
    private String content;
    private String gitRepoUrl;
    private String gitRepoName;
    private List<String> stack;
}
