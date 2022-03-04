package com.f5.onepageresumebe.web.dto.porf.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PorfCareerResponseDto {

    private String title;
    private String subTitle;
    private List<String> contents;
}
