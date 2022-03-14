package com.f5.onepageresumebe.web.dto.user.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class LoginResponseDto {
    private Boolean isFirstLogin;
}
