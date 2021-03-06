package com.f5.onepageresumebe.exception.customException;

import com.f5.onepageresumebe.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String reason;
    private ErrorCode errorCode;
}
