package com.f5.onepageresumebe.web.dto.porf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class PorfDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static class IntroRequest{
        @NotBlank(message = "포트폴리오 소개 제목이 필요합니다.")
        private String title;  ///소개 제목

        @NotBlank(message = "포트폴리오 소개글이 필요합니다.")
        private String contents;  //소개글 작성
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class ProjectRequest{

        @NotNull(message = "최소 1개 이상의 프로젝트 아이디가 필요합니다.")
        List<Integer> projectId;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class TemplateRequest{

        @NotNull(message = "포트폴리오 템플릿 종류를 입력해 주세요.")
        private Integer idx;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Status {

        private Boolean show;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Integer porfId;
        private String username;
        private List<String> userStack;
        private String title;
        private Integer templateIdx;
        private String job;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class IntroResponse{

        private Integer id;
        private String title;
        private String contents;
        private String githubUrl;
        private String blogUrl;
        private Integer viewCount;
        private String bgImage;
        private String modifiedAt;
        private Integer templateIdx;
        private String job;
        private String phoneNum;
        private String username;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class BookmarkResponse{

        private String title;
        private String content;
        private List<String> stack;
        private List<String> imgUrl;
        private Integer bookMarkCount;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class StackResponse{

        private List<String> mainStack;

        private List<String> subStack;

    }
}
