package com.f5.onepageresumebe.web.dto.MGit.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class MCommitMessageResponseDto {

    private String sha;
    private String message;

    public MCommitMessageResponseDto(String sha, String message) {
            this.sha = sha;
            this.message = message;
    }
}
