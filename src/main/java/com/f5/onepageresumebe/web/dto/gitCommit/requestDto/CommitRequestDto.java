package com.f5.onepageresumebe.web.dto.gitCommit.requestDto;

import com.f5.onepageresumebe.web.dto.gitFile.requestDto.FileRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitRequestDto {

    @NotBlank(message = "sha 코드가 필요합니다.")
    private String sha;
    @NotBlank(message = "commit message가 필요합니다.")
    private String commitMessage;
    @NotBlank(message = "트러블 슈팅 이름이 필요합니다.")
    private String tsName;
    @Valid
    private List<FileRequestDto> tsFile = new ArrayList<>();
}
