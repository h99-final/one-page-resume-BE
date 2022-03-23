package com.f5.onepageresumebe.domain.career.service;

import com.f5.onepageresumebe.domain.career.repository.CareerRepository;
import com.f5.onepageresumebe.domain.career.entity.Career;
import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.web.career.dto.CareerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional
    public Integer createCareer(CareerDto.Request requestDto) {

        //현재 로그인한 사람
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //커리어를 저장하기 위해 필요
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<String> contents = requestDto.getContents();
        if (contents.isEmpty()){
            throw new CustomException("직무 경험 내용을 하나 이상 입력해 주세요.", INVALID_INPUT_ERROR);
        }

        LocalDate startTime = requestDto.getStartTime();
        LocalDate endTime = convertEndTime(requestDto.getEndTime());

        //시작 시간이 종료 시간보다 앞 시간대인지 검증 -> dto받을때 해버릴까?
        validateDate(startTime,endTime);

        Career career = Career.create(requestDto.getTitle(),
                requestDto.getSubTitle(),
                careerContentsListToString(contents),
                startTime,
                endTime,
                portfolio);

        careerRepository.save(career);

        return career.getId();
    }

    @Transactional
    public void updateCareer(Integer careerId ,CareerDto.Request requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Career career = careerRepository.findByCareerIdAndUserEmail(careerId, userEmail).orElseThrow(() ->
                new IllegalArgumentException("내가 작성한 직무 경험만 수정할 수 있습니다"));

        List<String> contents = requestDto.getContents();
        if (contents.isEmpty()){
            throw new CustomException("직무 경험 내용을 하나 이상 입력해 주세요.", INVALID_INPUT_ERROR);
        }

        LocalDate startTime = requestDto.getStartTime();
        LocalDate endTime = convertEndTime(requestDto.getEndTime());

        validateDate(startTime,endTime);

        career.updateCareer(requestDto.getTitle(),
                requestDto.getSubTitle(),
                careerContentsListToString(requestDto.getContents()),
                startTime,
                endTime);
    }

    @Transactional
    public void deleteCareer(Integer careerId){

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Career career = careerRepository.findByCareerIdAndUserEmail(careerId, userEmail).orElseThrow(() ->
                new IllegalArgumentException("내가 작성한 직무 경험만 삭제할 수 있습니다"));

        careerRepository.deleteById(careerId);
    }

    public List<CareerDto.Response> getCareer(Integer porfId) {


        
        boolean isMyPorf = false;
        
        Portfolio portfolio = null;
        
        try {
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(()->
                    new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
            if(portfolio.getId() == porfId) isMyPorf = true;
        } catch (CustomAuthenticationException e) {
            isMyPorf = false;
        }

        portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<CareerDto.Response> careerResponseDtos = new ArrayList<>();

        if (isMyPorf || !(portfolio.getIsTemp())) {

            List<Career> careers = careerRepository.findAllByPorfIdOrderByEndTimeDesc(porfId);
            careers.forEach(career -> {
                String[] contents = career.getContents().split("----");
                List<String> contentsList = Arrays.asList(contents);

                LocalDate endTime = career.getEndTime();
                String endTimeString = null;
                if(endTime.isEqual(LocalDate.of(3000,1,1))){
                    endTimeString = "current";
                }else{
                    endTimeString = endTime.toString();
                }

                CareerDto.Response responseDto = CareerDto.Response.builder()
                        .id(career.getId())
                        .title(career.getTitle())
                        .subTitle(career.getSubTitle())
                        .contents(contentsList)
                        .startTime(career.getStartTime())
                        .endTime(endTimeString)
                        .build();
                careerResponseDtos.add(responseDto);
            });
        } else {
            return null;
        }

        return careerResponseDtos;
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

    private LocalDate convertEndTime(String endTimeString){

        LocalDate endTime = null;
        if("current".equals(endTimeString)){
            endTime = LocalDate.of(3000,1,1);
        }else{
            String[] split = endTimeString.split("-");
            Integer year = Integer.valueOf(split[0]);
            Integer month = Integer.valueOf(split[1]);
            Integer day = Integer.valueOf(split[2]);
            endTime = LocalDate.of(year,month,day);
        }

        return endTime;
    }

    private void validateDate(LocalDate startTime, LocalDate endTime){

        if(startTime.isAfter(endTime)){
            throw new CustomException("직무 경험 시작일은 직무 경험 종료일 보다 앞선 날짜여야 합니다.",INVALID_INPUT_ERROR);
        }
    }

}
