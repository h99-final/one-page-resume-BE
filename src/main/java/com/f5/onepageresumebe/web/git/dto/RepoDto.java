package com.f5.onepageresumebe.web.git.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

public class RepoDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        @NotBlank(message = "RepoUrl이 필요합니다.")
        private String gitRepoUrl;
        @NotBlank(message = "RepoName이 필요합니다.")
        private String gitRepoName;
    }
}
