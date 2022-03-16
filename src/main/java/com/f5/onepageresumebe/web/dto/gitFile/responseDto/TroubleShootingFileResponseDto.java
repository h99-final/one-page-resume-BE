package com.f5.onepageresumebe.web.dto.gitFile.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TroubleShootingFileResponseDto {
    private Integer fileId;
    private String fileName;
    private String tsContent;
    private List<String> tsPatchCodes;

    public TroubleShootingFileResponseDto(Integer fileId, String fileName, String tsContent, List<String> tsPatchCodes) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.tsContent = tsContent;
        this.tsPatchCodes = new ArrayList<>(tsPatchCodes);
    }
}
