package com.f5.onepageresumebe.web.dto.gitFile.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDto {
    private String fileName;
    private List<String> patchCode;
    private String tsContent;
}
