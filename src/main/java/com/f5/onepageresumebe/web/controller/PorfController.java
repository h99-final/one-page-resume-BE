package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.service.CareerService;
import com.f5.onepageresumebe.web.dto.career.requestDto.CareerRequestDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerListResponseDto;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.porf.ChangeStatusDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.*;
import com.f5.onepageresumebe.domain.service.PortfolioService;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfIntroResponseDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailListResponseDto;
import com.f5.onepageresumebe.web.dto.stack.StackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class PorfController {

    private final PortfolioService portfolioService;
    private final CareerService careerService;


    @Secured("ROLE_USER")
    @PostMapping("/porf/career")
    public ResDto createCareer(@RequestBody CareerRequestDto dto) {

        Integer careerId = careerService.createCareer(dto);

        return ResDto.builder()
                .result(true)
                .data(careerId)
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

    @GetMapping("/porf/{porfId}/intro")
    public ResDto getIntro(@PathVariable("porfId") Integer porfId){

        PorfIntroResponseDto responseDto = portfolioService.getIntro(porfId);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @PostMapping("/porf/intro/recommend")
    public ResDto getIntrosByStacks(@RequestBody StackDto requestDto){

        List<PorfResponseDto> responseDtos = portfolioService.getIntrosByStacks(requestDto);

        return ResDto.builder()
                .result(true)
                .data(responseDtos)
                .build();
    }

    @GetMapping("/porf/{porfId}/stack")
    public ResDto getStacks(@PathVariable("porfId") Integer porfId){

        StackDto stackContents = portfolioService.getStackContents(porfId);

        return ResDto.builder()
                .result(true)
                .data(stackContents)
                .build();
    }

    @GetMapping("/porf/{porfId}/career")
    public ResDto getCareer(@PathVariable("porfId") Integer porfId){

        CareerListResponseDto responseDto = careerService.getCareer(porfId);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @GetMapping("/porf/{porfId}/project")
    public ResDto getProject(@PathVariable("porfId") Integer porfId){

        ProjectDetailListResponseDto responseDto = portfolioService.getProject(porfId);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/intro")
    public ResDto updateIntro(@RequestBody PorfIntroRequestDto requestDto){

        portfolioService.updateIntro(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/template")
    public ResDto updateTemplate(@RequestBody PorfTemplateRequestDto requestDto){

        portfolioService.updateTemplate(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/stack")
    public ResDto updateStack(@RequestBody StackDto requestDto){

        portfolioService.updateStack(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/career/{careerId}")
    public ResDto updateCareer(@RequestBody CareerRequestDto requestDto,
                               @PathVariable("careerId") Integer careerId){

        careerService.updateCareer(careerId,requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/porf/career/{careerId}")
    public ResDto deleteCareer(@PathVariable("careerId") Integer careerId){

        careerService.deleteCareer(careerId);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/porf")
    public ResDto reset(){

        portfolioService.reset();

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }


}
