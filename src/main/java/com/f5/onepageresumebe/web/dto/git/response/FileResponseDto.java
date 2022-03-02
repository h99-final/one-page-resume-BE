package com.f5.onepageresumebe.web.dto.git.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class FileResponseDto {

    private Integer fileId;

    private String fileName;
}
