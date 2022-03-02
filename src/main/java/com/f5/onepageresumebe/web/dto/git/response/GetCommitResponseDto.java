package com.f5.onepageresumebe.web.dto.git.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class GetCommitResponseDto {

    List<CommitResponseDto> commits;
}
