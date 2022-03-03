package com.f5.onepageresumebe.web.dto.git.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CommitResponseDto {

    public String sha;
    public String message;
}
