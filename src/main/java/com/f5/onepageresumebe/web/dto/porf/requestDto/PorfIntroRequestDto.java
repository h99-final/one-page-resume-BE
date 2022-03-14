package com.f5.onepageresumebe.web.dto.porf.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PorfIntroRequestDto {//소개글 생성에 대한 dto

    @NotBlank(message = "포트폴리오 소개 제목이 필요합니다.")
    private String title;  ///소개 제목

    @NotBlank(message = "포트폴리오 소개글이 필요합니다.")
    private String contents;  //소개글 작성

}
