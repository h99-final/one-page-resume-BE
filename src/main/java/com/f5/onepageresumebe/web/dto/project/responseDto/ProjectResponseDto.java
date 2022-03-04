package com.f5.onepageresumebe.web.dto.project.responseDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProjectResponseDto {

    private Integer projectId;
    private String projectTitle;
}
