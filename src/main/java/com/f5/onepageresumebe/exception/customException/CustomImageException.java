package com.f5.onepageresumebe.exception.customException;

import com.f5.onepageresumebe.exception.ErrorCode;

public class CustomImageException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    public CustomImageException(String reason) {
        super(reason);
    }

}
