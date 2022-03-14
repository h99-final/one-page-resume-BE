package com.f5.onepageresumebe.web.dto.porf.requestDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PorfTemplateRequestDto {

    @NotNull(message = "포트폴리오 템플릿 종류를 입력해 주세요.")
    private Integer idx;
}
