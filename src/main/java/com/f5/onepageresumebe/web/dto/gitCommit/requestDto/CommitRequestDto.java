package com.f5.onepageresumebe.web.dto.gitCommit.requestDto;

import com.f5.onepageresumebe.web.dto.gitFile.requestDto.FileRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitRequestDto {
    private String sha;
    private String commitMessage;
    private String tsName;
    private List<FileRequestDto> tsFile = new ArrayList<>();
}
