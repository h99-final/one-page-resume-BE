package com.f5.onepageresumebe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponseDto {

    private boolean result;
    private ProjectSaveResponseDto data;
}
