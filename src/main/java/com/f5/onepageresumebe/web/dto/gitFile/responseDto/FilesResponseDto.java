package com.f5.onepageresumebe.web.dto.gitFile.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilesResponseDto {
    private String fileName;
    private String patchCode;

    public FilesResponseDto(String fileName, String patchCode) {
        this.fileName = fileName;
        this.patchCode = patchCode;
    }
}
