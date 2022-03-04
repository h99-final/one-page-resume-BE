package com.f5.onepageresumebe.web.controller;


import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfCareerRequestDto;
import com.f5.onepageresumebe.domain.service.CareerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Getter
public class CareerController {


    private final CareerService careerService;


    @PostMapping("/porf/career")
    public ResDto createCarrer(@RequestBody PorfCareerRequestDto porfCareerRequestDto)
    {
        careerService.createCarrer(porfCareerRequestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();



    }





}
