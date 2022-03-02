package com.f5.onepageresumebe.controller;


import com.f5.onepageresumebe.dto.careerDto.PorfCareerRequestDto;
import com.f5.onepageresumebe.dto.careerDto.commen.ResDto;
import com.f5.onepageresumebe.repository.CareerRepository;
import com.f5.onepageresumebe.service.CareerService;
import com.f5.onepageresumebe.service.PortfolioService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Getter
public class CarrerController {


    private final CareerService careerService;


    @PostMapping("/porf/career")
    public ResDto creatCarrer(@RequestBody PorfCareerRequestDto porfCareerRequestDto)
    {
        careerService.creatCarrer(porfCareerRequestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();



    }





}
