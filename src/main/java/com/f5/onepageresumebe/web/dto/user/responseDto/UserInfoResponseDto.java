package com.f5.onepageresumebe.web.dto.user.responseDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoResponseDto {
    private int id;
    private String email;
    private Boolean is_login;
}