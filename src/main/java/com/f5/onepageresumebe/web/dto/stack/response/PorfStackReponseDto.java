package com.f5.onepageresumebe.web.dto.stack.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PorfStackReponseDto {

    private List<String> mainStack;

    private List<String> subStack;
}
