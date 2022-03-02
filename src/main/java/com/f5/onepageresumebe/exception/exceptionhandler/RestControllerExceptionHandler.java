package com.f5.onepageresumebe.exception.exceptionhandler;

import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.ErrorResponse;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.exception.customException.CustomFieldException;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResDto methodArgumentNotValidException(MethodArgumentNotValidException e){

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.ofField(e.getBindingResult(), ErrorCode.INVALID_INPUT_ERROR))
                .build();
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResDto customException(CustomException e){

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.of(e.getReason(), e.getErrorCode()))
                .build();
    }

    @ExceptionHandler(CustomFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResDto customFieldException(CustomFieldException e){

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.ofField(e.getField(),e.getReason(),e.getErrorCode()))
                .build();
    }

    @ExceptionHandler(CustomAuthorizationException.class)
    public ResDto customAuthorizationException(CustomAuthorizationException e){

        return ResDto.builder()
                .result(false)
                .data(ErrorResponse.of(e.getMessage(),e.getErrorCode()))
                .build();
    }

}
