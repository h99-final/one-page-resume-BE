package com.f5.onepageresumebe.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_INPUT_ERROR(400,"E001"),
    DUPLICATED_INPUT_ERROR(400,"E002"),
    AUTHENTICATION_ERROR(401,"E003"),
    AUTHORIZATION_ERROR(403,"E004"),
    NOT_EXIST_ERROR(400,"E005");

    private int httpStatus;
    private String customErrorCode;

    ErrorCode(final int httpStatus, final String code){
        this.httpStatus = httpStatus;
        this.customErrorCode =code;
    }
}
