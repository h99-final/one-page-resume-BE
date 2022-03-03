package com.f5.onepageresumebe.web.dto.project.responseDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponseDto {

    private boolean result;
    private CreateProjectResponseDto data;
}
