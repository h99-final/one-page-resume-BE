package com.f5.onepageresumebe.web.dto.user.requestDto;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @Email(message = "이메일 형식으로 입력해 주세요")
    private String email;
    @NotBlank(message = "비밀번호를 입력해 주세요")
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(this.email,this.password);
    }
}