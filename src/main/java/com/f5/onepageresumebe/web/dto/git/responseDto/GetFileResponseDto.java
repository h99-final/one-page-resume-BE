package com.f5.onepageresumebe.web.dto.git.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class GetFileResponseDto {

    private List<FileResponseDto> files;
}
