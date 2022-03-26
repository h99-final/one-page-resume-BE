package com.f5.onepageresumebe.web.portfolio.controller;

import com.f5.onepageresumebe.web.common.dto.ResDto;
import com.f5.onepageresumebe.domain.portfolio.service.PortfolioService;
import com.f5.onepageresumebe.web.portfolio.dto.PorfDto;
import com.f5.onepageresumebe.web.project.dto.ProjectDto;
import com.f5.onepageresumebe.web.stack.dto.StackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RequiredArgsConstructor
@RestController
public class PorfController {

    private final PortfolioService portfolioService;

    @Secured("ROLE_USER")
    @PostMapping("/porf/show")
    public ResDto changeStatus(@Valid @RequestBody PorfDto.Status requestDto){

        PorfDto.Status changeStatusDto = portfolioService.changeStatus(requestDto);

        return ResDto.builder()
                .result(true)
                .data(changeStatusDto)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/project")
    public ResDto addProjectsInPortfolio(@Valid @RequestBody PorfDto.ProjectRequest requestDto){

        portfolioService.inputProjectInPorf(requestDto);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @GetMapping("/porf/{porfId}/intro")
    public ResDto getIntro(@PathVariable("porfId") Integer porfId){

        PorfDto.IntroResponse responseDto = portfolioService.getIntro(porfId);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @PostMapping("/porf/intro/recommend")
    public ResDto getIntrosByStacks(@RequestBody StackDto requestDto,
                                    @PageableDefault(size = 12) Pageable pageable){

        List<PorfDto.Response> responseDtos = portfolioService.getIntrosByStacks(requestDto,pageable);

        return ResDto.builder()
                .result(true)
                .data(responseDtos)
                .build();
    }

    @GetMapping("/porf/{porfId}/stack")
    public ResDto getStacks(@PathVariable("porfId") Integer porfId){

        PorfDto.StackResponse reponseDto = portfolioService.getStackContents(porfId);

        return ResDto.builder()
                .result(true)
                .data(reponseDto)
                .build();
    }

    @GetMapping("/porf/{porfId}/project")
    public ResDto getProject(@PathVariable("porfId") Integer porfId){

        List<ProjectDto.Response> responseDto = portfolioService.getProject(porfId);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/intro")
    public ResDto updateIntro(@Valid @RequestBody PorfDto.IntroRequest requestDto){

        portfolioService.updateIntro(requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/template")
    public ResDto updateTemplate(@Valid @RequestBody PorfDto.TemplateRequest requestDto){

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
    @DeleteMapping("/porf")
    public ResDto reset(){

        portfolioService.reset();

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }


}
