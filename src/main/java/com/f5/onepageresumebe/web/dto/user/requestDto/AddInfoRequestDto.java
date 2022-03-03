package com.f5.onepageresumebe.web.dto.user.requestDto;

import com.f5.onepageresumebe.web.dto.user.responseDto.AddInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddInfoRequestDto {
    private String name;
    private List<String> stack;
    private String phoneNum;
    private String gitUrl;
    private String blogUrl;
}
