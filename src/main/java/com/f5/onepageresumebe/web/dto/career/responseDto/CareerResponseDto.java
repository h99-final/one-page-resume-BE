package com.f5.onepageresumebe.web.dto.career.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CareerResponseDto<T> {

    private Integer id;
    private String title;
    private String subTitle;
    private List<String> contents;
    private LocalDate startTime;
    private String endTime;
}
