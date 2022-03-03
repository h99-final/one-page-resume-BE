package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.Career;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.repository.PortfolioRepository;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfCareerRequestDto;
import com.f5.onepageresumebe.domain.repository.CareerRepository;
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
    public void createCarrer(PorfCareerRequestDto porfCareerRequestDto) {


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
