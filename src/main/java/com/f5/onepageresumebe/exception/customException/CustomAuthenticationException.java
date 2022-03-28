package com.f5.onepageresumebe.exception.customException;

import com.f5.onepageresumebe.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;

    public CustomAuthenticationException(String reason) {
        super(reason);
    }
}
