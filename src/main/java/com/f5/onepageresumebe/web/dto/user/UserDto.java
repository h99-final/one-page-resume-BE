package com.f5.onepageresumebe.web.dto.user;

import com.f5.onepageresumebe.web.dto.jwt.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class UserDto {

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class AddInfoRequest{

        @NotBlank(message = "이름을 입력해 주세요")
        private String name;

        private List<String> stack;

        private String phoneNum;

        @NotBlank(message = "깃허브 주소를 입력해 주세요")
        private String gitUrl;

        private String blogUrl;

        @NotBlank(message = "직무 분야를 입력해 주세요")
        private String job;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateInfoRequest{
        @NotBlank(message = "이름을 입력해 주세요")
        private String name;

        private String phoneNum;

        @NotBlank(message = "깃허브 주소를 입력해 주세요")
        private String gitUrl;

        private String blogUrl;

        @NotBlank(message = "직무 분야를 입력해 주세요")
        private String job;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignUpRequest{

        @Email(message = "이메일을 입력해 주세요")
        private String email;

        @NotBlank(message = "비밀번호를 입력해 주세요")
        private String password;

        @NotBlank(message = "비밀번호 확인을 입력해 주세요")
        private String passwordCheck;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginRequest{
        @Email(message = "이메일 형식으로 입력해 주세요")
        private String email;
        @NotBlank(message = "비밀번호를 입력해 주세요")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(this.email,this.password);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GitTokenRequest{
        private String token;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FindEmailRequest{
        private String name;
        private String phoneNum;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EmailRequest{

        @Email(message = "이메일 형식으로 입력해 주세요")
        String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CertificationRequest{

        @NotBlank(message = "이메일 형식으로 입력해 주세요")
        @Email(message = "이메일 형식으로 입력해 주세요")
        String email;

        @NotBlank(message = "인증 코드를 입력해 주세요")
        String code;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PasswordRequest{

        @NotBlank(message = "현재 비밀번호를 입력해 주세요")
        private String curPassword;

        @NotBlank(message = "비밀번호를 입력해 주세요")
        private String password;

        @NotBlank(message = "비밀번호 확인을 입력해 주세요")
        private String passwordCheck;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EmailResponse{
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResponse{
        private Boolean isFirstLogin;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResult{
        private TokenDto tokenDto;
        private LoginResponse loginResponseDto;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ImgResponse{
        private String img;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoResponse{
        Integer userId;
        Integer porfId;
        boolean porfShow;
        List<Integer> projectId;
        String email;
        String name;
        String phoneNum;
        String gitUrl;
        String blogUrl;
        List<String> stack;
        String job;
        String profileImage;
        Boolean isToken;
    }
}
