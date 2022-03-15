package com.f5.onepageresumebe.web.dto.user.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FindEmailRequestDto {
    private String name;
    private String phoneNum;
}
