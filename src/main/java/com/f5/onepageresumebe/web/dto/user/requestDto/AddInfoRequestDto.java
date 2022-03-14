package com.f5.onepageresumebe.web.dto.user.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddInfoRequestDto {

    @NotBlank(message = "이름을 입력해 주세요")
    private String name;

    private List<String> stack;

    private String phoneNum;

    @NotBlank(message = "깃허브 주소를 입력해 주세요")
    private String gitUrl;

    private String blogUrl;

    @NotBlank(message = "직무 분야를 입력해 주세요")
    private String job;
}
