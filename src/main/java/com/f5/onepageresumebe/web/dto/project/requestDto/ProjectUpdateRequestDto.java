package com.f5.onepageresumebe.web.dto.project.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectUpdateRequestDto {

    @NotBlank(message = "프로젝트 제목이 필요합니다.")
    private String title;

    @NotBlank(message = "프로젝트 내용이 필요합니다.")
    private String content;

    private List<String> stack;
}
