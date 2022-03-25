package com.f5.onepageresumebe.domain.career.service;

import com.f5.onepageresumebe.domain.career.repository.CareerRepository;
import com.f5.onepageresumebe.domain.career.entity.Career;
import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
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
import static com.f5.onepageresumebe.exception.ErrorCode.NOT_EXIST_ERROR;

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
                () -> new CustomException("포트폴리오가 존재하지 않습니다",NOT_EXIST_ERROR));

        //String 값으로 들어온 datetime을 LocalDate로 변환
        LocalDate startTime = convertStringToTime(requestDto.getStartTime());
        LocalDate endTime = convertStringToTime(requestDto.getEndTime());

        //시작 시간이 끝나는 시간보다 늦지는 않는지 확인, 내용이 비어있는지 확인
        validateRequestDto(startTime,endTime,requestDto.getContents());

        Career career = Career.create(requestDto.getTitle(),
                requestDto.getSubTitle(),
                careerContentsListToString(requestDto.getContents()),
                startTime,
                endTime,
                portfolio);

        careerRepository.save(career);

        return career.getId();
    }

    @Transactional
    public void updateCareer(Integer careerId ,CareerDto.Request requestDto) {

        //로그인한 유저 확인
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //자신이 작성한 커리어인지 확인
        Career career = careerRepository.findByCareerIdAndUserEmail(careerId, userEmail).orElseThrow(() ->
                new CustomAuthorizationException("내가 작성한 직무 경험만 수정할 수 있습니다"));

        //String 값으로 들어온 datetime을 LocalDate로 변환
        LocalDate startTime = convertStringToTime(requestDto.getStartTime());
        LocalDate endTime = convertStringToTime(requestDto.getEndTime());

        //시작 시간이 끝나는 시간보다 늦지는 않는지 확인, 내용이 비어있는지 확인
        validateRequestDto(startTime,endTime,requestDto.getContents());

        career.updateCareer(requestDto.getTitle(),
                requestDto.getSubTitle(),
                careerContentsListToString(requestDto.getContents()),
                startTime,
                endTime);
    }

    @Transactional
    public void deleteCareer(Integer careerId){

        //로그인한 유저 확인
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //현재 커리어를 작성한 유저인지 확인
        Boolean exists = careerRepository.existsByCareerIdAndUserEmail(careerId, userEmail);
        if(exists){
            careerRepository.deleteById(careerId);
        }else{
            throw new CustomAuthorizationException("내가 작성한 직무 경험만 삭제할 수 있습니다");
        }
    }

    public List<CareerDto.Response> getCareer(Integer porfId) {

        //나의 포트폴리오인지 확인
        boolean isMyPorf = isMyPorf(porfId);

        //조회하고자 하는 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new CustomException("존재하지 않는 포트폴리오입니다.",NOT_EXIST_ERROR));

        List<CareerDto.Response> careerResponseDtos = new ArrayList<>();

        //나의 포트폴리오이거나 공개된 포트폴리오일때
        if (isMyPorf || !(portfolio.getIsTemp())) {

            List<Career> careers = careerRepository.findAllByPorfIdOrderByEndTimeDesc(porfId);
            careers.forEach(career -> {
                CareerDto.Response responseDto = careerToResponseDto(career);
                careerResponseDtos.add(responseDto);
            });
        } else {
            //나의 포트폴리오가 아니고, 공개되지 않았을때
            return null;
        }

        return careerResponseDtos;
    }

    private boolean isMyPorf(Integer porfId){

        try {
            //로그인 상태
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            //현재 로그인한 사람의 포트폴리오인지 확인
            return portfolioRepository.existsByUserEmailAndPorfId(userEmail, porfId);

        } catch (CustomAuthenticationException e) {
            //비로그인
            return false;
        }
    }

    private CareerDto.Response careerToResponseDto(Career career){

        //구분자를 ---로 했기 때문에 가져올때 다시 split
        String[] contents = career.getContents().split("----");
        List<String> contentsList = Arrays.asList(contents);

        //3000-01-01은 현재 진행중을 뜻하므로 변환
        LocalDate endTime = career.getEndTime();
        String endTimeString = null;
        if(endTime.isEqual(LocalDate.of(3000,1,1))){
            endTimeString = "current";
        }else{
            endTimeString = endTime.toString();
        }

        return CareerDto.Response.builder()
                .id(career.getId())
                .title(career.getTitle())
                .subTitle(career.getSubTitle())
                .contents(contentsList)
                .startTime(career.getStartTime())
                .endTime(endTimeString)
                .build();
    }

    private String careerContentsListToString(List<String> contentsList) {

        StringBuilder sb = new StringBuilder();
        int size = contentsList.size();

        //커리어 내용 리스트를 --- 구분자를 통해 하나의 string으로 변환하는 작업
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                sb.append(contentsList.get(i));
            } else {
                sb.append(contentsList.get(i) + "----");
            }
        }
        return sb.toString();
    }

    private LocalDate convertStringToTime(String timeString){

        LocalDate time = null;
        //current면 3000-01-01로 변환
        if("current".equals(timeString)){
            time = LocalDate.of(3000,1,1);
        }else{
            //아니라면 날짜 포멧에 맞추어 변환
            String[] split = timeString.split("-");
            Integer year = Integer.valueOf(split[0]);
            Integer month = Integer.valueOf(split[1]);
            Integer day = Integer.valueOf(split[2]);
            time = LocalDate.of(year,month,day);
        }

        return time;
    }

    private void validateRequestDto(LocalDate startTime, LocalDate endTime, List<String> contents){

        if(startTime.isAfter(endTime)){
            throw new CustomException("직무 경험 시작일은 직무 경험 종료일 보다 앞선 날짜여야 합니다.",INVALID_INPUT_ERROR);
        }

        if(contents.isEmpty()){
            throw new CustomException("직무 경험 내용을 하나 이상 입력해 주세요.", INVALID_INPUT_ERROR);
        }
    }

}
