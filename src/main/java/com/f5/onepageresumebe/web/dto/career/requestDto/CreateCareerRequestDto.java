package com.f5.onepageresumebe.web.dto.career.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCareerRequestDto {

    private List<CareerRequestDto> careers;
}
