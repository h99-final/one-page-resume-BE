package com.f5.onepageresumebe.web.dto.project.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectUpdateRequestDto {

    private String title;
    private String content;
    private List<String> stack;
}
