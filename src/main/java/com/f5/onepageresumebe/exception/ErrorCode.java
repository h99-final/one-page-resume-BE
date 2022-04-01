package com.f5.onepageresumebe.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, "E001"),
    INVALID_INPUT_ERROR(400, "E002"),
    DUPLICATED_INPUT_ERROR(400, "E003"),
    AUTHENTICATION_ERROR(401, "E004"),
    AUTHORIZATION_ERROR(403, "E005"),
    NOT_EXIST_ERROR(400, "E006"),
    TOO_MANY_CALL(400, "E007");

    private final int httpStatus;
    private final String customErrorCode;

    ErrorCode(final int httpStatus, final String code) {
        this.httpStatus = httpStatus;
        this.customErrorCode = code;
    }
}
