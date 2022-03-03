package com.f5.onepageresumebe.web.dto.user.responseDto;

import com.f5.onepageresumebe.web.dto.user.requestDto.AddInfoRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddInfoResponseDto {
    private List<String > stack;

    public AddInfoResponseDto(List<String> stack) {
        this.stack = stack;
    }
}
