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

    private String title;  ///소개 제목
    private String contents;  //소개글 작성

}
