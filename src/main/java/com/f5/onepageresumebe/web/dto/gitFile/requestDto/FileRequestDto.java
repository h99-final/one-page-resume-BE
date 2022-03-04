package com.f5.onepageresumebe.web.dto.gitFile.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDto {
    private String fileName;
    private String patchCode;
    private String tsContent;
}
