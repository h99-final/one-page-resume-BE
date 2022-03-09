package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;

import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.PorfUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.util.StackUtil;
import com.f5.onepageresumebe.web.dto.career.requestDto.CareerRequestDto;
import com.f5.onepageresumebe.web.dto.career.requestDto.CareerListRequestDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerListResponseDto;
import com.f5.onepageresumebe.web.dto.career.responseDto.CareerResponseDto;
import com.f5.onepageresumebe.web.dto.porf.ChangeStatusDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfIntroRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfProjectRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfTemplateRequestDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfIntroResponseDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailListResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailResponseDto;
import com.f5.onepageresumebe.web.dto.stack.StackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StackRepository stackRepository;
    private final PortfolioStackRepository portfolioStackRepository;
    private final UserRepository userRepository;
    private final UserStackRepository userStackRepository;
    private final CareerRepository careerRepository;
    private final ProjectRepository projectRepository;
    private final ProjectImgRepository projectImgRepository;
    private final ProjectStackRepository projectStackRepository;

    @Transactional
    public void updateIntro(PorfIntroRequestDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포트폴리오입니다"));

        portfolio.updateIntro(requestDto.getTitle(), portfolio.getUser().getGithubUrl(), requestDto.getContents(), portfolio.getUser().getBlogUrl());

        portfolioRepository.save(portfolio);
    }

    @Transactional //템플릿 테마 지정
    public void updateTemplate(PorfTemplateRequestDto porfTemplateRequestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        portfolio.updateTemplate(porfTemplateRequestDto.getIdx());
        portfolioRepository.save(portfolio);

    }


    @Transactional
    public void updateStack(StackDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        //기존에 있는 스택 모두 삭제
        portfolioStackRepository.deleteAllByPorfId(portfolio.getId());

        insertStacksInPortfolio(portfolio, requestDto.getStack());
    }

    @Transactional
    public ChangeStatusDto changeStatus(ChangeStatusDto dto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        String changedStatus = portfolio.changeStatus(dto.getStatus());

        return ChangeStatusDto.builder()
                .status(changedStatus)
                .build();
    }

    @Transactional
    public void inputProjectInPorf(PorfProjectRequestDto requestDto) {
        String email = SecurityUtil.getCurrentLoginUserId();
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        List<Project> projects = projectRepository.findAllByIds(projectIds);

        projects.forEach(project -> {
            if (!project.getUser().getId().equals(portfolio.getUser().getId())) {
                throw new IllegalArgumentException("내가 작성한 프로젝트만 가져올 수 있습니다");
            }
            project.setPortfolio(portfolio);
        });
    }

    @Transactional
    public void deleteProjectInPorf(PorfProjectRequestDto requestDto) {
        String email = SecurityUtil.getCurrentLoginUserId();
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        List<Project> projects = projectRepository.findAllByIds(projectIds);

        projects.forEach(project -> project.removePortfolio(portfolio));
    }

    @Transactional
    public PorfIntroResponseDto getIntro(Integer porfId) {

        boolean myPorf = PorfUtil.isMyPorf(porfId,portfolioRepository);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        if (myPorf || !(portfolio.getIsTemp())) {
            try {
                String email = SecurityUtil.getCurrentLoginUserId();
                Portfolio myPortfolio = portfolioRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                        new IllegalArgumentException("포트폴리오가 존재하지 않습니다."));
                if (myPortfolio.getId()!=portfolio.getId()) portfolio.increaseViewCount();
            } catch (CustomAuthenticationException e) {
                portfolio.increaseViewCount();
            }

            portfolioRepository.save(portfolio);
            return PorfIntroResponseDto.builder()
                    .title(portfolio.getTitle())
                    .githubUrl(portfolio.getGithubUrl())
                    .blogUrl(portfolio.getBlogUrl())
                    .contents(portfolio.getIntroContents())
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
    public List<PorfResponseDto> getIntrosByStacks(StackDto requestDto) {

        List<String> stackNames = requestDto.getStack();
        List<PorfResponseDto> responseDtoList = new ArrayList<>();
        List<Portfolio> portfolioList;
        if (stackNames.size() == 0) {
            //특정 조건이 없을 때
            //공개 된 것들만 가져온다
            portfolioList = portfolioRepository.findAllFetchUserIfPublic();
        } else {
            //특정 스택을 가진, 공개된 포트폴리오만 조회
            portfolioList = portfolioRepository.findAllByStackNamesIfPublic(stackNames);
        }

        Collections.shuffle(portfolioList); // 순서 섞음

        portfolioList.forEach(portfolio -> {
            List<String> stacks = userStackRepository.findStackNamesByPorfId(portfolio.getId());
            PorfResponseDto responseDto = PorfResponseDto.builder()
                    .porfId(portfolio.getId())
                    .username(portfolio.getUser().getName())
                    .userStack(stacks)
                    .title(portfolio.getTitle())
                    .templateIdx(portfolio.getTemplateIdx())
                    .build();
            responseDtoList.add(responseDto);
        });

        return responseDtoList;
    }

    public StackDto getStackContents(Integer porfId) {

        boolean myPorf = PorfUtil.isMyPorf(porfId,portfolioRepository);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        if (myPorf || !(portfolio.getIsTemp())) {

            List<String> stackNames = portfolioStackRepository.findStackNamesByPorfId(porfId);

            return StackDto.builder()
                    .stack(stackNames)
                    .build();
        } else {
            return null;
        }


    }



    public ProjectDetailListResponseDto getProject(Integer porfId) {

        boolean myPorf = PorfUtil.isMyPorf(porfId,portfolioRepository);
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        if (myPorf || !(portfolio.getIsTemp())) {
            List<Project> projects = projectRepository.findAllByPorfId(porfId);
            return ProjectDetailListResponseDto.builder()
                    .projects(ProjectUtil.projectToDetailResponseDtos(projects, projectImgRepository, projectStackRepository))
                    .build();
        } else {
            return null;
        }
    }



    @Transactional
    public void reset() {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포트폴리오 입니다"));

        Integer porfId = portfolio.getId();
        portfolio.reset();

        //연결된 프로젝트 모두 연결 끊기
        List<Project> projects = projectRepository.findAllByPorfId(porfId);
        projects.forEach(project -> project.removePortfolio(portfolio));

        //연결된 커리어 모두 삭제
        careerRepository.deleteAllByPorfId(porfId);

        //연결된 기술 스택 모두 연결 끊기
        portfolioStackRepository.deleteAllByPorfId(porfId);
    }

    private void insertStacksInPortfolio(Portfolio portfolio, List<String> stackNames) {
        //중복 스택 입력시, 중복데이터 제거
        stackNames = stackNames.stream().distinct().collect(Collectors.toList());

        stackNames.forEach(name -> {
            Stack stack = StackUtil.createStack(name, stackRepository);
            PortfolioStack portfolioStack = PortfolioStack.create(portfolio, stack);
            portfolioStackRepository.save(portfolioStack);
        });
    }

}
