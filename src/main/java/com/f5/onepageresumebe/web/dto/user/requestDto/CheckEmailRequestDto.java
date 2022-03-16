package com.f5.onepageresumebe.web.dto.user.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckEmailRequestDto {

    @NotBlank(message = "이메일 형식으로 입력해 주세요")
    @Email(message = "이메일 형식으로 입력해 주세요")
    String email;
}
