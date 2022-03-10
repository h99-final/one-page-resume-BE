package com.f5.onepageresumebe.web.dto.career.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CareerRequestDto {

    @NotBlank(message = "제목이 필요합니다.")
    private String title;

    @NotBlank(message = "부제목이 필요합니다.")
    private String subTitle;

    private List<String> contents;

    @NotNull(message = "경력 시작일이 필요합니다.")
    private LocalDate startTime;

    @NotNull(message = "경력 종료일이 필요합니다.")
    private LocalDate endTime;
}
