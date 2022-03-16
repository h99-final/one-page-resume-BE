package com.f5.onepageresumebe.exception.customException;

import com.f5.onepageresumebe.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class CustomAuthorizationException extends AccessDeniedException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode = ErrorCode.AUTHORIZATION_ERROR;

    public CustomAuthorizationException(String reason) {
        super(reason);
    }
}
