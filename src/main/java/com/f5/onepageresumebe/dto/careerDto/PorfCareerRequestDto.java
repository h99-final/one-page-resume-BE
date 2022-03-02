package com.f5.onepageresumebe.dto.careerDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class PorfCareerRequestDto {


    private Integer id;
    private String title;
    private String subTitle;
    List<String> contents;
    private LocalDate startTime;
    private LocalDate endTime;


}
