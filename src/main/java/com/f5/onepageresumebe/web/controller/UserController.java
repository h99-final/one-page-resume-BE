package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.AddInfoRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.CheckEmailRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.LoginRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.SignupRequestDto;
import com.f5.onepageresumebe.domain.service.UserService;
import com.f5.onepageresumebe.web.dto.user.responseDto.LoginResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입 요청 처리
    @PostMapping("/user/signup")
    public ResDto registerUser(@RequestBody SignupRequestDto request) {

        return ResDto.builder()
                .result(userService.registerUser(request))
                .build();
    }

    //이메일 중복 체크
    @PostMapping("/user/dupEmail")
    public ResDto checkEmail(@RequestBody CheckEmailRequestDto request) {

        return ResDto.builder()
                .result(userService.checkEmail(request))
                .build();
    }

    // 로그인
//    private static final int COOKIE_TIME = 60 * 5;
    @PostMapping("/user/login")
    public ResponseEntity login(@RequestBody LoginRequestDto requestDto) {

//        Cookie token = new Cookie("Authorization",userService.createToken(loginDto.getEmail()));
//
//        token.setMaxAge(COOKIE_TIME);
//        token.setDomain("localhost");
//        response.addCookie(token);

        LoginResultDto loginResultDto = userService.login(requestDto);
        HttpHeaders headers = userService.tokenToHeader(loginResultDto.getTokenDto());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ResDto.builder()
                        .result(true)
                        .data(loginResultDto.getResponseDto())
                        .build());


    }

    //추가 기입
    @Secured("ROLE_USER")
    @PostMapping("/user/info")
    public ResDto addInfo(@RequestBody AddInfoRequestDto requestDto) {

        return ResDto.builder()
                .result(true)
                .data(userService.addInfo(requestDto))
                .build();
    }

    //개인 정보 수정
    @Secured("ROLE_USER")
    @PutMapping("/user/info")
    public ResDto updateInfo(@RequestBody AddInfoRequestDto request) {

        userService.updateInfo(request);

        return ResDto.builder()
                .result(true)
                .build();
    }
}