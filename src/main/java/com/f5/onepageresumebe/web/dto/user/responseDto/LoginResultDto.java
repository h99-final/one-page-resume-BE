package com.f5.onepageresumebe.web.dto.user.responseDto;

import com.f5.onepageresumebe.web.dto.jwt.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginResultDto {

    private TokenDto tokenDto;
    private LoginResponseDto loginResponseDto;
}
