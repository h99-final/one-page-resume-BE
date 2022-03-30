package com.f5.onepageresumebe.web.project.dto;

import com.f5.onepageresumebe.web.git.dto.FileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ProjectDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{

        private Integer id;
        private String title;
        private String imageUrl;
        private List<String> stack;
        private String content;
        private Integer bookmarkCount;
        private String username;
        private String userJob;
        private Boolean isMyProject;
        private Boolean isBookmarking;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetailResponse{
        private Integer id;
        private String title;
        private List<ImgResponse> img;
        private List<String> stack;
        private Integer bookmarkCount;
        private String content;
        private String username;
        private String userJob;
        private String gitRepoUrl;
        private Boolean isMyProject;
        private Boolean isBookmarking;

        public void checkBookmark(boolean isMyProject, boolean isBookmarking){
            this.isMyProject = isMyProject;
            this.isBookmarking = isBookmarking;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TroubleShootingsResponse{
        private Integer commitId;
        private String commitMsg;
        private String sha;
        private String tsName;
        private List<FileDto.TroubleShooting> tsFiles;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ImgResponse{

        private Integer id;
        private String url;

    }
}
