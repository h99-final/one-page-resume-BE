package com.f5.onepageresumebe.web.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
@AllArgsConstructor
@Data
@Builder
public class ResDto<T> {

    private Boolean result;
    @Nullable
    private T data;
}