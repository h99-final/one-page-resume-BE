package com.f5.onepageresumebe.dto.careerDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProjectSaveResponseDto {


    private Integer projectId;
    private String projectTitle;
}
