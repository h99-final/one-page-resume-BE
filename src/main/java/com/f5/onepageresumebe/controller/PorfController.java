package com.f5.onepageresumebe.controller;


import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.dto.PorfIntroRequestDto;
import com.f5.onepageresumebe.dto.PorfStackRequestDto;
import com.f5.onepageresumebe.dto.PorfTemplateRequestDto;
import com.f5.onepageresumebe.dto.commen.ResDto;
import com.f5.onepageresumebe.service.PortfolioService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
@Getter
public class PorfController {


    private final PortfolioService portfolioService;
    private  final S3Uploader s3Uploader;


    @GetMapping("/porf/test")
    public void test(){
        System.out.println("확인");
    }


    @PostMapping("/porf/intro") //포트폴리오 소개 작성
    public ResDto createIntro(@RequestPart(value = "img", required = false) MultipartFile multipartFile
                             , @RequestPart(value = "intro", required = false)PorfIntroRequestDto porfIntroRequestDto)
            throws IOException {

       portfolioService.createIntro(multipartFile,porfIntroRequestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @PostMapping("/porf/template") //포트폴리오 템플릿 작성
    public ResDto createTemplate(@RequestBody PorfTemplateRequestDto porfTemplateRequestDto)
    {

        portfolioService.createTemplate(porfTemplateRequestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }


   @PostMapping("/porf/stack")
    public  ResDto createStack(@RequestBody PorfStackRequestDto porfStackRequestDto){


        portfolioService.createStack(porfStackRequestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }
}
