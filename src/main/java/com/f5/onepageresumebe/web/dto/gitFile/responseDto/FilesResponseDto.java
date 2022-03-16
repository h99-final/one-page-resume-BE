package com.f5.onepageresumebe.web.dto.gitFile.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FilesResponseDto {
    private String name;
    private List<String> patchCode;

    public FilesResponseDto(String name, List<String> patchCode) {
        this.name = name;
        this.patchCode = patchCode;
    }
}
