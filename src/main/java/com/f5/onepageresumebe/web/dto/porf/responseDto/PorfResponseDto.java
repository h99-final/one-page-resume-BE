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
public class PorfResponseDto {

    private Integer porfId;
    private String username;
    private List<String> userStack;
    private String introduceTitle;
    private Integer templateIdx;
}
