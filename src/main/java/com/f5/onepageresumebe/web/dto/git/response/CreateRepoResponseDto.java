package com.f5.onepageresumebe.web.dto.git.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateRepoResponseDto {

    private Integer repoId;
}
