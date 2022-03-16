package com.f5.onepageresumebe.web.dto.porf.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PorfIntroResponseDto {

    private Integer id;
    private String title;
    private String contents;
    private String githubUrl;
    private String blogUrl;
    private Integer viewCount;
    private String bgImage;
    private String modifiedAt;
    private Integer templateIdx;
    //추가
    private String job;
    private String phoneNum;
    private String username;
    private String email;

}
