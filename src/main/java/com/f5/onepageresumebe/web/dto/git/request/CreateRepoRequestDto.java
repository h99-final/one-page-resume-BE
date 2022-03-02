package com.f5.onepageresumebe.web.dto.git.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class CreateRepoRequestDto {

    @NotNull(message = "레포지토리 URL을 입력해 주세요")
    private String repoUrl;
}
