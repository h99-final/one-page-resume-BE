package com.f5.onepageresumebe.web.dto.gitFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class FileDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static class Request{
        @NotBlank(message = "파일 이름이 필요합니다.")
        private String fileName;

        @NotNull(message = "patchCode가 필요합니다.")
        private List<String> patchCode;

        @NotBlank(message = "트러블 슈팅 내용이 필요합니다.")
        private String tsContent;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String name;
        private List<String> patchCode;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class TroubleShooting{
        private Integer fileId;
        private String fileName;
        private String tsContent;
        private List<String> tsPatchCodes;
    }
}
