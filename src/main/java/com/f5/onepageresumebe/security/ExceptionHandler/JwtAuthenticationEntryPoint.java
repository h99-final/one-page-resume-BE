package com.f5.onepageresumebe.security.ExceptionHandler;

import com.f5.onepageresumebe.exception.ErrorResponse;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.f5.onepageresumebe.exception.ErrorCode.AUTHENTICATION_ERROR;


@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMsg;

        if (exception instanceof BadCredentialsException ||
                exception instanceof InternalAuthenticationServiceException) {
            errorMsg = "아이디나 비밀번호가 맞지 않습니다. 다시 확인해 주세요";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMsg = "비밀번호 유효기간이 지났습니다. 비밀번호를 재설정해주세요";
        } else {
            errorMsg = "로그인이 필요합니다";
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.setStatus(AUTHENTICATION_ERROR.getHttpStatus());
        ErrorResponse exceptionRes = ErrorResponse.of(errorMsg, AUTHENTICATION_ERROR);
        ResDto<Object> resDto = ResDto.builder()
                .result(false)
                .data(exceptionRes)
                .build();

        String errorJson = om.writeValueAsString(resDto);

        PrintWriter writer = response.getWriter();
        writer.print(errorJson);
    }
}
