package com.f5.onepageresumebe.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class ErrorResponse<T> {

    private T errors;
    private String errorCode;

    // 객체를 생성할 때 이름을 부여하여 특정한 목적에서 구별하여 생성할 수 있도록 하자
    public static ErrorResponse ofField(BindingResult bindingResult, ErrorCode errorCode) {
        return ErrorResponse.builder()
                .errors(CustomFieldError.of(bindingResult))
                .errorCode(errorCode.getCustomErrorCode())
                .build();
    }

    public static ErrorResponse of(String message, ErrorCode errorCode){

        return ErrorResponse.builder()
                .errors(CustomError.of(message))
                .errorCode(errorCode.getCustomErrorCode())
                .build();
    }

    //에러 목록을 리스트로 처리하여, 여러 에러를 동시에 객체에 담을 수 있도록 한다
    @Getter
    @Builder(access = AccessLevel.PROTECTED)
    public static class CustomFieldError {
        private String field;
        private String message;

        public static List<CustomFieldError> of(String field, String reason) {

            List<CustomFieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(CustomFieldError.builder()
                    .field(field)
                    .message(reason)
                    .build());

            return fieldErrors;
        }

        public static List<CustomFieldError> of(BindingResult bindingResult) {

            List<CustomFieldError> customFieldErrors = new ArrayList<>();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError error : fieldErrors) {

                customFieldErrors.add(
                        CustomFieldError.builder()
                                .field(error.getField())
                                .message(error.getDefaultMessage())
                                .build()
                );
            }

            return customFieldErrors;
        }
    }


    @Builder(access = AccessLevel.PROTECTED)
    @Getter
    public static class CustomError {
        private String message;

        public static List<CustomError> of(String message) {

            List<CustomError> customErrorList = new ArrayList<>();
            customErrorList.add(CustomError.builder()
                    .message(message)
                    .build());

            return customErrorList;
        }
    }

}
