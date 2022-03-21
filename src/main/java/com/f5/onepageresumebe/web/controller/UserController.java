package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.web.dto.MGit.request.MGitTokenDto;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.stack.StackDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.*;
import com.f5.onepageresumebe.domain.mysql.service.UserService;
import com.f5.onepageresumebe.web.dto.user.responseDto.FindEmailResponseDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.LoginResultDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입 요청 처리
    @PostMapping("/user/signup")
    public ResDto registerUser(@Valid @RequestBody SignupRequestDto request) {

        return ResDto.builder()
                .result(userService.registerUser(request))
                .build();
    }

    //이메일 중복 체크
    @PostMapping("/user/dupEmail")
    public ResDto checkEmail(@Valid @RequestBody CheckEmailRequestDto request) {

        return ResDto.builder()
                .result(userService.checkEmail(request))
                .build();
    }

    // 로그인
//    private static final int COOKIE_TIME = 60 * 5;
    @PostMapping("/user/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequestDto requestDto) {

        LoginResultDto loginResultDto = userService.login(requestDto);
        HttpHeaders headers = userService.tokenToHeader(loginResultDto.getTokenDto());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ResDto.builder()
                        .result(true)
                        .data(loginResultDto.getLoginResponseDto())
                        .build());


    }

    //추가 기입
    @Secured("ROLE_USER")
    @PostMapping("/user/info")
    public ResDto addInfo(@Valid @RequestBody AddInfoRequestDto requestDto) {

        userService.addInfo(requestDto);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/user/git/token")
    public ResDto updateToken(@RequestBody MGitTokenDto requestDto){

        userService.updateGitToken(requestDto.getToken());

        return ResDto.builder()
                .result(true)
                .build();
    }

    //개인 정보 수정
    @Secured("ROLE_USER")
    @PutMapping("/user/info")
    public ResDto updateInfo(@Valid @RequestBody UpdateInfoRequestDto requestDto) {

        userService.updateInfo(requestDto);

        return ResDto.builder()
                .result(true)
                .build();
    }

    //개인 스택 수정
    @Secured("ROLE_USER")
    @PutMapping("/user/stack")
    public ResDto updateStack(@Valid @RequestBody StackDto requestDto) {

        userService.updateStacks(requestDto);

        return ResDto.builder()
                .result(true)
                .build();
    }


    //유저 정보
    @Secured("ROLE_USER")
    @GetMapping("/user/info")
    public ResDto getInfo(){

        UserInfoResponseDto userInfo = userService.getUserInfo();

        return ResDto.builder()
                .result(true)
                .data(userInfo)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/user/profile")
    public ResDto updateProfile(@RequestPart("profileImage") MultipartFile multipartFile){

        if(multipartFile.isEmpty()){
            throw new CustomException("빈 이미지 파일입니다. 다시 업로드 해주세요", INVALID_INPUT_ERROR);
        }

        return ResDto.builder()
                .result(true)
                .data(userService.updateProfile(multipartFile))
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/user/profile")
    public ResDto deleteProfile(){

        userService.deleteProfile();

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/user/password")
    public ResDto changePassword(@Valid @RequestBody ChangePasswordRequestDto requestDto) {

        userService.ChangePassword(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @PostMapping("/user/auth/email")
    public ResDto certificationEmail(@RequestBody CertificationRequestDto requestDto) {

        userService.certificationEmail(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @PostMapping("/user/auth/email/valid")
    public ResDto checkCertification(@RequestBody CheckCertificationRequestDto requestDto) {

        return ResDto.builder()
                .result(userService.checkCertification(requestDto))
                .data(null)
                .build();
    }

    @PostMapping("/user/password/find")
    public ResDto findPassword(@RequestBody CertificationRequestDto requestDto) {

        userService.findPassword(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @PostMapping("/user/email/find")
    public ResDto findEmail(@RequestBody FindEmailRequestDto requestDto) {

        return ResDto.builder()
                .result(true)
                .data(userService.findEmail(requestDto))
                .build();
    }
}