package com.f5.onepageresumebe.web.dto.gitFile.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDto {

    @NotBlank(message = "파일 이름이 필요합니다.")
    private String fileName;

    @NotNull(message = "patchCode가 필요합니다.")
    private List<String> patchCode;

    @NotBlank(message = "트러블 슈팅 내용이 필요합니다.")
    private String tsContent;
}
