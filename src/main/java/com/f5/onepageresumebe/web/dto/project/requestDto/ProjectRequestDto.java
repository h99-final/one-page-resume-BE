package com.f5.onepageresumebe.web.dto.project.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class ProjectRequestDto {

    @NotBlank(message = "프로젝트 제목이 필요합니다.")
    private String title;

    @NotBlank(message = "프로젝트 내용이 필요합니다.")
    private String content;

    @NotBlank(message = "프로젝트의 깃허브 Repository Url이 필요합니다.")
    private String gitRepoUrl;

    @NotBlank(message = "프로젝트의 깃허브 Repository 이름이 필요합니다.")
    private String gitRepoName;

    @NotNull(message = "프로젝트 스택이 필요합니다.")
    private List<String> stack;
}
