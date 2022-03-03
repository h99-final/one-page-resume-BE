package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.PortfolioStack;
import com.f5.onepageresumebe.domain.entity.Stack;
import com.f5.onepageresumebe.domain.repository.CareerRepository;

import com.f5.onepageresumebe.domain.repository.PortfolioRepository;
import com.f5.onepageresumebe.domain.repository.PortfolioStackRepository;
import com.f5.onepageresumebe.domain.repository.StackRepository;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfIntroRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfStackRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfTemplateRequestDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfIntroResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
public class PortfolioService {


    private final PortfolioRepository portfolioRepository;
    private final StackRepository stackRepository;
    private final PortfolioStackRepository portfolioStackRepository;
    private final S3Uploader s3Uploader;


   @Transactional//소개문 작성
    public PorfIntroResponseDto createIntro(MultipartFile multipartFile, PorfIntroRequestDto porfIntroRequestDto) throws IOException {


       //유저 아이디로 포폴 아이디 가져오기
        Portfolio portfolio = portfolioRepository.findById(porfIntroRequestDto.getId()).orElseThrow(
                () -> new NullPointerException("없습니다")
        );


       String imageUrl = s3Uploader.upload(multipartFile,"introImage");//사진 업로드


       portfolio.updateIntro(porfIntroRequestDto.getTitle(), porfIntroRequestDto.getGithubUrl(),
                porfIntroRequestDto.getBlogUrl(), imageUrl,
                porfIntroRequestDto.getIntroContents(), portfolio.getUser());

       portfolioRepository.save(portfolio);

       return new PorfIntroResponseDto(portfolio.getId());
   }

    @Transactional //템플릿 작성
    public void createTemplate(PorfTemplateRequestDto porfTemplateRequestDto) {


        Portfolio portfolio = portfolioRepository.findById(porfTemplateRequestDto.getId()).orElseThrow(
                () -> new NullPointerException("없습니다")
        );



        portfolio.updateTemplate(porfTemplateRequestDto.getTemplateIdx());
        portfolioRepository.save(portfolio);


    }


    @Transactional//기술 스택 작성
    public void createStack(PorfStackRequestDto porfStackRequestDto) {


        Portfolio portfolio = portfolioRepository.findById(porfStackRequestDto.getId()).orElseThrow(
                () -> new NullPointerException("없습니다")
        );


        List<String> stackContents = porfStackRequestDto.getStackContents();
        //Optional<Stack> found = stackRepository.findByName(stackContents);

        for(String content: stackContents){
            Stack stack = stackRepository.findFirstByName(content).orElse(null);


            //기존에 같은 이름의 스텍이 없으면
            if (stack == null) {
                stack = Stack.create(content);
                stackRepository.save(stack);


                //StackRepository.findByName(porfStackRequestDto.getStack())
            }


            PortfolioStack portfolioStack =PortfolioStack.create(portfolio, stack);

            portfolioStackRepository.save(portfolioStack);

        }

    }


}