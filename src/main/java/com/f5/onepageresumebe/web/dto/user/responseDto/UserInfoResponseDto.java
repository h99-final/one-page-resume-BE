package com.f5.onepageresumebe.web.dto.user.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class UserInfoResponseDto {

    Integer userId;
    Integer porfId;
    List<Integer> projectId;
    String email;
    String name;
    String phoneNum;
    String gitUrl;
    String blogUrl;
    List<String> stack;
    String job;
    String profileImage;
}