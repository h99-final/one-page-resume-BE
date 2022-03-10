package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.service.CareerService;
import com.f5.onepageresumebe.web.dto.career.requestDto.CareerRequestDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerResponseDto;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @Secured("ROLE_USER")
    @PostMapping("/porf/career")
    public ResDto createCareer(@Valid @RequestBody CareerRequestDto dto) {

        Integer id = careerService.createCareer(dto);

        return ResDto.builder()
                .result(true)
                .data(id)
                .build();
    }

    @GetMapping("/porf/{porfId}/career")
    public ResDto getCareer(@PathVariable("porfId") Integer porfId){

        List<CareerResponseDto>  responseDto = careerService.getCareer(porfId);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/porf/career/{careerId}")
    public ResDto updateCareer(@Valid @RequestBody CareerRequestDto requestDto,
                               @PathVariable("careerId") Integer careerId){

        careerService.updateCareer(careerId, requestDto);

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
}
