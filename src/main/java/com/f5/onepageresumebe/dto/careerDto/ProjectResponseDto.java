package com.f5.onepageresumebe.dto.careerDto;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponseDto {

    private boolean result;
    private ProjectSaveResponseDto data;
}
