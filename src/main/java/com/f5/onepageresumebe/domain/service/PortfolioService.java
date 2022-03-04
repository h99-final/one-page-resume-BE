package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;

import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.web.dto.career.requestDto.CreateCareerRequestDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerListResponseDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerResponseDto;
import com.f5.onepageresumebe.web.dto.porf.ChangeStatusDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfIntroRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfProjectRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfStackRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfTemplateRequestDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfIntroResponseDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfResponseDto;
import com.f5.onepageresumebe.web.dto.stack.StackContentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StackRepository stackRepository;
    private final PortfolioStackRepository portfolioStackRepository;
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final ProjectRepository projectRepository;
    private final S3Uploader s3Uploader;


    @Transactional//소개문 작성
    public void createIntro(PorfIntroRequestDto porfIntroRequestDto) throws IOException {

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail).get();

        //유저 이메일로 포폴 아이디 가져오기
        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        portfolio.updateIntro(porfIntroRequestDto.getIntroTitle(), user.getGithubUrl(),
                user.getBlogUrl(),
                porfIntroRequestDto.getIntroContents());

        portfolioRepository.save(portfolio);

    }

    @Transactional //템플릿 테마 지정
    public void createTemplate(PorfTemplateRequestDto porfTemplateRequestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        portfolio.updateTemplate(porfTemplateRequestDto.getTemplateIdx());
        portfolioRepository.save(portfolio);


    }


    @Transactional//기술 스택 작성
    public void createStack(PorfStackRequestDto porfStackRequestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        List<String> stackNames = porfStackRequestDto.getStackContents();

        stackNames.stream().forEach(name -> {
            Stack stack = stackRepository.findFirstByName(name).orElse(null);
            //이미 존재하는 스택이라면
            if (stack != null) {
                PortfolioStack portfolioStack = portfolioStackRepository.findFirstByPortfolioAndStack(portfolio, stack)
                        .orElse(null);
                //연결되지 않은 스택일경우
                if (portfolioStack == null) {
                    PortfolioStack createdPortfolioStack = PortfolioStack.create(portfolio, stack);
                    portfolioStackRepository.save(createdPortfolioStack);
                }
            } else {
                //존재하지 않는 스택이라면
                Stack createdStack = Stack.create(name);
                stackRepository.save(createdStack);
                PortfolioStack createdPortfolioStack = PortfolioStack.create(portfolio, createdStack);
                portfolioStackRepository.save(createdPortfolioStack);
            }
        });
    }

    @Transactional
    public void createCareer(CreateCareerRequestDto requestDto) {

        //현재 로그인한 사람
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        requestDto.getCareers().stream().forEach(dto -> {

            List<String> contentsList = dto.getContents();
            StringBuilder sb = new StringBuilder();
            int size = contentsList.size();

            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    sb.append(contentsList.get(i));
                } else {
                    sb.append(contentsList.get(i) + "----");
                }
            }
            String combinedContents = sb.toString();

            Career career = Career.create(dto.getTitle(), dto.getSubTitle(), combinedContents,
                    dto.getStartTime(), dto.getEndTime(), portfolio);

            careerRepository.save(career);

        });
    }

    @Transactional
    public ChangeStatusDto changeStatus(ChangeStatusDto dto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        String changedStatus = portfolio.changeStatus(dto.getStatus());

        return ChangeStatusDto.builder()
                .status(changedStatus)
                .build();
    }

    @Transactional
    public void inputProjectInPorf(PorfProjectRequestDto requestDto) {
        String email = SecurityUtil.getCurrentLoginUserId();
        Portfolio portfolio = portfolioRepository.findByUserEmail(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        List<Project> projects = projectRepository.findAllByIds(projectIds);

        projects.stream().forEach(project -> {
            if (project.getUser().getId() != portfolio.getUser().getId()) {
                throw new IllegalArgumentException("내가 작성한 프로젝트만 가져올 수 있습니다");
            }
            project.setPortfolio(portfolio);
        });
    }

    @Transactional
    public void deleteProjectInPorf(PorfProjectRequestDto requestDto) {
        String email = SecurityUtil.getCurrentLoginUserId();
        Portfolio portfolio = portfolioRepository.findByUserEmail(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        List<Project> projects = projectRepository.findAllByIds(projectIds);

        projects.stream().forEach(project -> project.removePortfolio(portfolio));
    }

    @Transactional
    public PorfIntroResponseDto getIntro(Integer porfId) {

        Integer myPorf = isMyPorf(porfId);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        if (myPorf == 1
                || ((myPorf==0 || myPorf==-1) && (!portfolio.getIsTemp()))) {
            portfolio.increaseViewCount();
            portfolioRepository.save(portfolio);
            return PorfIntroResponseDto.builder()
                    .introduceTitle(portfolio.getTitle())
                    .githubUrl(portfolio.getGithubUrl())
                    .blogUrl(portfolio.getBlogUrl())
                    .introContents(portfolio.getIntroContents())
                    .bgImage(portfolio.getUser().getProfileImgUrl()) // 유저의 프로필 이미지
                    .viewCount(portfolio.getViewCount())
                    .modifiedAt(portfolio.getUpdatedAt().toString())
                    .templateIdx(portfolio.getTemplateIdx())
                    .build();
        } else {
            return null;
        }


    }

    //전체 조회
    public List<PorfResponseDto> getIntrosByStacks(PorfStackRequestDto requestDto) {

        List<String> stackNames = requestDto.getStackContents();
        List<PorfResponseDto> responseDtoList = new ArrayList<>();
        List<Portfolio> portfolioList = null;
        if (stackNames.size() == 0) {
            //특정 조건이 없을 때
            //공개 된 것들만 가져온다
            portfolioList = portfolioRepository.findAllFetchUserIfPublic();
        } else {
            //특정 스택을 가진, 공개된 포트폴리오만 조회
            portfolioList = portfolioRepository.findAllByStackNamesIfPublic(stackNames);
        }

        Collections.shuffle(portfolioList); // 순서 섞음

        portfolioList.stream().forEach(portfolio -> {
            List<String> stacks = portfolioStackRepository.findStackNamesByPorfId(portfolio.getId());
            PorfResponseDto responseDto = PorfResponseDto.builder()
                    .porfId(portfolio.getId())
                    .username(portfolio.getUser().getName())
                    .userStack(stacks)
                    .introduceTitle(portfolio.getTitle())
                    .templateIdx(portfolio.getTemplateIdx())
                    .build();
            responseDtoList.add(responseDto);
        });

        return responseDtoList;
    }

    public StackContentsDto getStackContents(Integer porfId){

        Integer myPorf = isMyPorf(porfId);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        if (myPorf == 1
                || ((myPorf==0 || myPorf==-1) && (!portfolio.getIsTemp()))) {

            List<String> stackNames = portfolioStackRepository.findStackNamesByPorfId(porfId);

            return StackContentsDto.builder()
                    .stackContents(stackNames)
                    .build();
        } else {
            return null;
        }


    }

    public CareerListResponseDto getCareer(Integer porfId){

        Integer myPorf = isMyPorf(porfId);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<CareerResponseDto> careerResponseDtos = new ArrayList<>();

        if (myPorf == 1
                || ((myPorf==0 || myPorf==-1) && (!portfolio.getIsTemp()))) {

            List<Career> careers = careerRepository.findAllByPorfId(porfId);
            careers.stream().forEach(career -> {
                String[] contents = career.getContents().split("----");
                CareerResponseDto responseDto = CareerResponseDto.builder()
                        .title(career.getTitle())
                        .subTitle(career.getSubTitle())
                        .contents(Arrays.asList(contents))
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

    private Integer isMyPorf(Integer porfId){

        try{
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).get();
            if (portfolio.getUser().getId()==porfId){
                return 1;
            }else {
                return 0;
            }

        }catch (CustomAuthenticationException e){
            return -1;
        }
    }



}



