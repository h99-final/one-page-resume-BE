package com.f5.onepageresumebe.web.dto.user.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDto {

    @NotBlank(message = "비밀번호를 입력해 주세요")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요")
    private String passwordCheck;
}
