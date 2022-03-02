package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.security.UserDetailsImpl;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.AddInfoRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.CheckEmailRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.LoginRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.SignupRequestDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.LoginResponseDto;
import com.f5.onepageresumebe.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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
    public ResDto login(@RequestBody LoginRequestDto request, HttpServletResponse response) {

//        Cookie token = new Cookie("Authorization",userService.createToken(loginDto.getEmail()));
//
//        token.setMaxAge(COOKIE_TIME);
//        token.setDomain("localhost");
//        response.addCookie(token);

        response.setHeader("Authorization",userService.createToken(request.getEmail()));
        return ResDto.builder()
                .result(true)
                .data(userService.login(request))
                .build();
    }

    //추가 기입
    @PostMapping("/user/info")
    public ResDto addInfo(@RequestBody AddInfoRequestDto reuqest, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        return ResDto.builder()
                .result(true)
                .data(userService.addInfo(reuqest, user))
                .build();
    }

    //개인 정보 수정
    @PutMapping("/user/info")
    public ResDto updateInfo(@RequestBody AddInfoRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        userService.updateInfo(request, user);

        return ResDto.builder()
                .result(true)
                .build();
    }
}