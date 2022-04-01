package com.f5.onepageresumebe.exception.exceptionhandler;

import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.ErrorResponse;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.web.common.dto.ResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResDto methodArgumentNotValidException(MethodArgumentNotValidException e) {

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.ofField(e.getBindingResult(), ErrorCode.INVALID_INPUT_ERROR))
                .build();
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity customException(CustomException e) {

        int httpStatus = e.getErrorCode().getHttpStatus();

        return ResponseEntity.status(httpStatus)
                .body(ResDto.builder()
                        .result(false)
                        .data(ErrorResponse.of(e.getReason(), e.getErrorCode()))
                        .build());
    }

    @ExceptionHandler(CustomAuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResDto customAuthorizationException(CustomAuthorizationException e) {

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.of(e.getMessage(), e.getErrorCode()))
                .build();
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResDto customAuthenticationException(CustomAuthenticationException e) {

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.of(e.getMessage(), e.getErrorCode()))
                .build();
    }

}
