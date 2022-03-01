package com.f5.onepageresumebe.controller;


import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.dto.careerDto.PorfIntroRequestDto;
import com.f5.onepageresumebe.dto.careerDto.PorfIntroResponseDto;
import com.f5.onepageresumebe.dto.careerDto.commen.ResDto;
import com.f5.onepageresumebe.repository.PortfolioRepository;
import com.f5.onepageresumebe.service.PortfolioService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
@Getter
public class PorfController {


    private final PortfolioService portfolioService;
    private  final S3Uploader s3Uploader;


    @PostMapping("/porf/intro")
    public ResDto createIntro(@RequestPart("img") MultipartFile multipartFile, @RequestPart("intro") PorfIntroRequestDto porfIntroRequestDto, Integer id, PorfIntroResponseDto porfIntroResponseDto)
            throws IOException {

        portfolioService.createIntro(porfIntroRequestDto,id,porfIntroResponseDto);
        String image = s3Uploader.upload(multipartFile,"introImage");
        porfIntroRequestDto.setIntroBgImgUrl(image);

        return ResDto.builder()
                .result(true)
                .data(porfIntroResponseDto)
                .build();
    }
}
