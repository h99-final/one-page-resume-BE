package com.f5.onepageresumebe.web.dto.gitFile.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FilesResponseDto {
    private String fileName;
    private List<String> patchCode;

    public FilesResponseDto(String fileName, List<String> patchCode) {
        this.fileName = fileName;
        this.patchCode = patchCode;
    }
}
