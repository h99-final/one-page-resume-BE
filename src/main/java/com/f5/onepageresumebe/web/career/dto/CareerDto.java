package com.f5.onepageresumebe.web.career.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;


public class CareerDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        @NotBlank(message = "제목이 필요합니다.")
        private String title;

        @NotBlank(message = "부제목이 필요합니다.")
        private String subTitle;

        private List<String> contents;

        @NotNull(message = "경력 시작일이 필요합니다.")
        private String startTime;

        @NotNull(message = "경력 종료일이 필요합니다.")
        private String endTime;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private Integer id;
        private String title;
        private String subTitle;
        private List<String> contents;
        private LocalDate startTime;
        private String endTime;
    }
}
