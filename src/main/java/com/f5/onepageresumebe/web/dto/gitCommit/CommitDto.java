package com.f5.onepageresumebe.web.dto.gitCommit;


import com.f5.onepageresumebe.web.dto.gitFile.FileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class CommitDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        @NotBlank(message = "sha 코드가 필요합니다.")
        private String sha;
        @NotBlank(message = "commit message가 필요합니다.")
        private String commitMessage;
        @NotBlank(message = "트러블 슈팅 이름이 필요합니다.")
        private String tsName;
        @Valid
        private List<FileDto.Request> tsFile = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class IdResponse{
        private Integer commitId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MessageResponse{
        private String sha;
        private String message;
    }
}
