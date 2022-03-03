package com.f5.onepageresumebe.web.dto.porf.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PorfIntroRequestDto {//소개글 생성에 대한 dto

    private Integer id;
    private String title;  ///소개 제목
    private String githubUrl; //깃허브 url
    private String blogUrl; // 블로그 url
    private String introContents;  //소개글 작성

}
