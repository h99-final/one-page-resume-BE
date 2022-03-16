package com.f5.onepageresumebe.web.dto.porf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChangeStatusDto {

    @NotBlank(message = "public, private 중 어느 상태로 바꿀것인지 선택해 주세요.")
    private String status;
}
