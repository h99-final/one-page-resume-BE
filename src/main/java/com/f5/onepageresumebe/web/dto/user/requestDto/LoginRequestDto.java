package com.f5.onepageresumebe.web.dto.user.requestDto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    private String email;
    private String password;
}