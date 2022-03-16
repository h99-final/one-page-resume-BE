package com.f5.onepageresumebe.exception.exceptionhandler;

import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.ErrorResponse;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.exception.customException.CustomFieldException;
import com.f5.onepageresumebe.web.dto.common.ResDto;
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

    @ExceptionHandler(CustomFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResDto customFieldException(CustomFieldException e) {

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.ofField(e.getField(), e.getReason(), e.getErrorCode()))
                .build();
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

//    @ExceptionHandler(Exception.class)
//    public ResDto globalException(Exception e){
//
//        log.error("알수 없는 오류 : {}",e.getMessage());
//        e.printStackTrace();
//
//        return ResDto.builder()
//                .result(false)
//                .data(ErrorResponse.of("알 수 없는 오류가 발생하였습니다. 관리자에게 문의해 주세요.", INTERNAL_SERVER_ERROR))
//                .build();
//    }

}
