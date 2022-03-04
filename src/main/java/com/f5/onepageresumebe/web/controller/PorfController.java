package com.f5.onepageresumebe.web.controller;


import com.f5.onepageresumebe.web.dto.career.requestDto.CreateCareerRequestDto;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.porf.ChangeStatusDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfIntroRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfProjectRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfStackRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfTemplateRequestDto;
import com.f5.onepageresumebe.domain.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
public class PorfController {

    private final PortfolioService portfolioService;

    @Secured("ROLE_USER")
    @PostMapping("/porf/intro") //포트폴리오 소개 작성
    public ResDto createIntro(@RequestBody PorfIntroRequestDto dto)
            throws IOException {

        portfolioService.createIntro(dto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/porf/template") //포트폴리오 템플릿 작성
    public ResDto createTemplate(@RequestBody PorfTemplateRequestDto porfTemplateRequestDto) {

        portfolioService.createTemplate(porfTemplateRequestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/porf/stack")
    public ResDto createStack(@RequestBody PorfStackRequestDto porfStackRequestDto) {


        portfolioService.createStack(porfStackRequestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/porf/career")
    public ResDto createCareer(@RequestBody CreateCareerRequestDto dto) {

        portfolioService.createCareer(dto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/porf/status")
    public ResDto changeStatus(@RequestBody ChangeStatusDto requestDto){

        ChangeStatusDto changeStatusDto = portfolioService.changeStatus(requestDto);

        return ResDto.builder()
                .result(true)
                .data(changeStatusDto)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/porf/project")
    public ResDto inputProjectInPortfolio(@RequestBody PorfProjectRequestDto requestDto){

        portfolioService.inputProjectInPorf(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/porf/project")
    public ResDto deleteProjectInPortfolio(@RequestBody PorfProjectRequestDto requestDto){

        portfolioService.deleteProjectInPorf(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }
}
