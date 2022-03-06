package com.f5.onepageresumebe.web.dto.gitCommit.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommitMessageResponseDto {
    private String sha;
    private String message;

    public CommitMessageResponseDto(String sha, String message) {
        this.sha = sha;
        this.message = message;
    }
}
