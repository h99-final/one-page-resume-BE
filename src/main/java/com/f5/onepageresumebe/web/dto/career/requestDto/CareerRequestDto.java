package com.f5.onepageresumebe.web.dto.career.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CareerRequestDto {

    private String title;
    private String subTitle;
    private List<String> contents;
    private LocalDate startTime;
    private LocalDate endTime;
}
