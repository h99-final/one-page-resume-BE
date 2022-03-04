package com.f5.onepageresumebe.web.dto.porf.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PorfProjectRequestDto {

    List<Integer> projectId;
}
