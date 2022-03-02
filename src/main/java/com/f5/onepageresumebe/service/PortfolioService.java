package com.f5.onepageresumebe.service;

import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.dto.careerDto.PorfIntroRequestDto;
import com.f5.onepageresumebe.dto.careerDto.PorfIntroResponseDto;
import com.f5.onepageresumebe.dto.careerDto.PorfTemplateRequestDto;
import com.f5.onepageresumebe.repository.PortfolioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
public class PortfolioService {


    private final PortfolioRepository portfolioRepository;



    @Transactional
    public void createIntro(PorfIntroRequestDto porfIntroRequestDto, Integer id, PorfIntroResponseDto porfIntroResponseDto){

        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(
                ()-> new NullPointerException("없습니다")
        );

        porfIntroResponseDto.setId(id);



       //String ListsplitContent[] = porfIntroRequestDto.getIntroContents().split("/n");

       portfolio.updateInrtro(porfIntroRequestDto.getTitle(),porfIntroRequestDto.getGithubUrl(),porfIntroRequestDto.getBlogUrl(), porfIntroRequestDto.getIntroBgImgUrl(),porfIntroRequestDto.getIntroContents(),portfolio.getUser());



    }

    @Transactional
    public   void  createTemplate(PorfTemplateRequestDto porfTemplateRequestDto, Integer id) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(
                ()-> new NullPointerException("없습니다")
        );

        portfolio.updateTemplate(porfTemplateRequestDto.getTemplateIdx());
    }
}
