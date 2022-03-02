package com.f5.onepageresumebe.web.dto.user.responseDto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class LoginResponseDto {
    //로그인 되었을때 리턴되는객체들 토큰발행
    private Boolean isFirstLogin;
    private Integer portfolioId;
    private Integer userId;
    private String email;
    List<String> stack = new ArrayList<>();

    public LoginResponseDto(Boolean isFirstLogin, Integer portfolioId, Integer userId, String email, List<String > stack) {
        this.isFirstLogin = isFirstLogin;
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.email = email;
        this.stack = stack;
    }
}