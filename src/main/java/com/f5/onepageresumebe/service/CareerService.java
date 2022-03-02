package com.f5.onepageresumebe.service;

import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.Career;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.dto.careerDto.PorfCareerRequestDto;
import com.f5.onepageresumebe.repository.CareerRepository;
import com.f5.onepageresumebe.repository.PortfolioRepository;
import com.f5.onepageresumebe.repository.PortfolioStackRepository;
import com.f5.onepageresumebe.repository.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;



@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
public class CareerService {

    private final PortfolioRepository portfolioRepository;
    private final CareerRepository careerRepository;


    @Transactional//커리어 작성
    public void creatCarrer(PorfCareerRequestDto porfCareerRequestDto) {


        Portfolio portfolio = portfolioRepository.findById(porfCareerRequestDto.getId()).orElseThrow(
                () -> new NullPointerException("없습니다")
        );


        List<String> contents = porfCareerRequestDto.getContents();
        String contentsStr = "";
        for (int i=0; i<contents.size(); i++) {
            contentsStr += contents.get(i);
            if (i != contents.size() - 1) {
                contentsStr+= "\n";
            }
        }

        System.out.println(contentsStr); // 1\n2\n3
        Career career = Career.create(porfCareerRequestDto.getTitle(), porfCareerRequestDto.getSubTitle(), contentsStr,
                porfCareerRequestDto.getStartTime(), porfCareerRequestDto.getEndTime(), portfolio);
        careerRepository.save(career);

        //Optional<Stack> found = stackRepository.findByName(stackContents);

    }
}
