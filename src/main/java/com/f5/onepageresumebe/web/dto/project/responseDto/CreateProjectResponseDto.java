package com.f5.onepageresumebe.web.dto.project.responseDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateProjectResponseDto {


    private Integer projectId;
    private String projectTitle;
}
