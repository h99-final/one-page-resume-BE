package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.Career;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.repository.CareerRepository;
import com.f5.onepageresumebe.domain.repository.PortfolioRepository;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.PorfUtil;
import com.f5.onepageresumebe.web.dto.career.requestDto.CareerRequestDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerListResponseDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional
    public Integer createCareer(CareerRequestDto requestDto) {

        //현재 로그인한 사람
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        Career career = Career.create(requestDto.getTitle(),
                requestDto.getSubTitle(),
                careerContentsListToString(requestDto.getContents()),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                portfolio);

        careerRepository.save(career);

        return career.getId();
    }

    @Transactional
    public void updateCareer(Integer careerId ,CareerRequestDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Career career = careerRepository.findByIdAndUserEmail(careerId, userEmail).orElseThrow(() ->
                new IllegalArgumentException("내가 작성한 직무 경험만 수정할 수 있습니다"));

        career.updateCareer(requestDto.getTitle(),
                requestDto.getSubTitle(),
                careerContentsListToString(requestDto.getContents()),
                requestDto.getStartTime(),
                requestDto.getEndTime());
    }

    @Transactional
    public void deleteCareer(Integer careerId){

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Career career = careerRepository.findByIdAndUserEmail(careerId, userEmail).orElseThrow(() ->
                new IllegalArgumentException("내가 작성한 직무 경험만 삭제할 수 있습니다"));

        careerRepository.deleteById(careerId);
    }

    public CareerListResponseDto getCareer(Integer porfId) {

        boolean myPorf = PorfUtil.isMyPorf(porfId,portfolioRepository);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<CareerResponseDto> careerResponseDtos = new ArrayList<>();

        if (myPorf || !(portfolio.getIsTemp())) {

            List<Career> careers = careerRepository.findAllByPorfId(porfId);
            careers.forEach(career -> {
                String[] contents = career.getContents().split("----");
                List<String> contentsList = Arrays.asList(contents);

                CareerResponseDto responseDto = CareerResponseDto.builder()
                        .id(career.getId())
                        .title(career.getTitle())
                        .subTitle(career.getSubTitle())
                        .contents(contentsList)
                        .build();
                careerResponseDtos.add(responseDto);
            });
        } else {
            return null;
        }

        return CareerListResponseDto.builder()
                .careers(careerResponseDtos)
                .build();
    }

    private String careerContentsListToString(List<String> contentsList) {

        StringBuilder sb = new StringBuilder();
        int size = contentsList.size();

        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                sb.append(contentsList.get(i));
            } else {
                sb.append(contentsList.get(i) + "----");
            }
        }
        return sb.toString();
    }
}
