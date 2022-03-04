package com.f5.onepageresumebe.web.dto.stack;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StackContentsDto {
    private List<String> stackContents;
}
