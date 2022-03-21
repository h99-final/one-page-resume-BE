package com.f5.onepageresumebe.web.dto.project.responseDto;

import com.f5.onepageresumebe.web.dto.gitFile.responseDto.TroubleShootingFileResponseDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Builder
@Data
public class TroubleShootingsResponseDto {
    private Integer commitId;
    private String commitMsg;
    private String sha;
    private String tsName;
    private List<TroubleShootingFileResponseDto> tsFiles;

    public TroubleShootingsResponseDto(Integer commitId,String sha,String commitMsg, String tsName, List<TroubleShootingFileResponseDto> tsFiles) {
        this.commitId = commitId;
        this.tsName = tsName;
        this.sha = sha;
        this.commitMsg = commitMsg;
        this.tsFiles = new ArrayList<>(tsFiles);
    }
}
