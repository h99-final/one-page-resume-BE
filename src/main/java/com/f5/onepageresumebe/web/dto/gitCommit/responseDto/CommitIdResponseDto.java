package com.f5.onepageresumebe.web.dto.gitCommit.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CommitIdResponseDto {
    private Integer commitId;

    public CommitIdResponseDto(Integer commitId) {
        this.commitId = commitId;
    }
}
