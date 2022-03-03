package com.f5.onepageresumebe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PorfCareerRequestDto {


    private Integer id;
    private String title;
    private String subTitle;
    List<String> contents;
    private LocalDate startTime;
    private LocalDate endTime;


}
