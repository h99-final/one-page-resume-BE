package com.f5.onepageresumebe.dto.careerDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class PorfIntroRequestDto {//소개글 생성에 대한 dto

    private String title;  ///소개 제목
    private String githubUrl; //깃허브 url
    private String blogUrl; // 블로그 url
    private String introContents;  //소개글 작성
    private String introBgImgUrl;  //사진 url


}
