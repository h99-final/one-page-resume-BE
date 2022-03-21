package com.f5.onepageresumebe.web.dto.user.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateInfoRequestDto {

    @NotBlank(message = "이름을 입력해 주세요")
    private String name;

    private String phoneNum;

    @NotBlank(message = "깃허브 주소를 입력해 주세요")
    private String gitUrl;

    private String blogUrl;

    @NotBlank(message = "직무 분야를 입력해 주세요")
    private String job;
}
