package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;

import com.f5.onepageresumebe.domain.repository.querydsl.PortfolioQueryRepository;
import com.f5.onepageresumebe.domain.repository.querydsl.UserQueryRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.PorfUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.util.StackUtil;
import com.f5.onepageresumebe.web.dto.porf.ChangeStatusDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfIntroRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfProjectRequestDto;
import com.f5.onepageresumebe.web.dto.porf.requestDto.PorfTemplateRequestDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfIntroResponseDto;
import com.f5.onepageresumebe.web.dto.porf.responseDto.PorfResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import com.f5.onepageresumebe.web.dto.stack.StackDto;
import com.f5.onepageresumebe.web.dto.stack.response.PorfStackReponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;


@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StackRepository stackRepository;
    private final PortfolioStackRepository portfolioStackRepository;
    private final CareerRepository careerRepository;
    private final ProjectRepository projectRepository;
    private final ProjectImgRepository projectImgRepository;
    private final ProjectStackRepository projectStackRepository;
    private final PortfolioQueryRepository portfolioQueryRepository;
    private final UserQueryRepository userQueryRepository;

    @Transactional
    public void updateIntro(PorfIntroRequestDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포트폴리오입니다"));

        portfolio.updateIntro(requestDto.getTitle(), portfolio.getUser().getGithubUrl(), requestDto.getContents(), portfolio.getUser().getBlogUrl());

        portfolioRepository.save(portfolio);
    }

    @Transactional //템플릿 테마 지정
    public void updateTemplate(PorfTemplateRequestDto porfTemplateRequestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        portfolio.updateTemplate(porfTemplateRequestDto.getIdx());
        portfolioRepository.save(portfolio);

    }


    @Transactional
    public void updateStack(StackDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<String> stacks = requestDto.getStack();
        if (stacks.size()<3){
            throw new CustomException("포트폴리오 스택을 3개 이상 선택해 주세요.",INVALID_INPUT_ERROR);
        }

        //기존에 있는 스택 모두 삭제
        portfolioStackRepository.deleteAllByPorfId(portfolio.getId());

        insertStacksInPortfolio(portfolio, stacks);
    }

    @Transactional
    public ChangeStatusDto changeStatus(ChangeStatusDto dto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        String status = dto.getStatus();
        if(!("public".equals(status) || "private".equals(status)) ){
            throw new CustomException("포트폴리오 상태값은 public 이거나 private 입니다.", INVALID_INPUT_ERROR);
        }

        String changedStatus = portfolio.changeStatus(status);

        return ChangeStatusDto.builder()
                .status(changedStatus)
                .build();
    }

    @Transactional
    public void inputProjectInPorf(PorfProjectRequestDto requestDto) {
        String email = SecurityUtil.getCurrentLoginUserId();
        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        if (projectIds.isEmpty()){
            throw new CustomException("최소 하나의 프로젝트를 선택해 주세요.",INVALID_INPUT_ERROR);
        }

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
        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        if (projectIds.isEmpty()){
            throw new CustomException("최소 하나의 프로젝트를 선택해 주세요.",INVALID_INPUT_ERROR);
        }

        List<Project> projects = projectRepository.findAllByIds(projectIds);

        projects.forEach(project -> project.removePortfolio(portfolio));
    }

    @Transactional
    public PorfIntroResponseDto getIntro(Integer porfId) {

        boolean myPorf = PorfUtil.isMyPorf(porfId,portfolioQueryRepository);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        if (myPorf || !(portfolio.getIsTemp())) {
            try {
                String email = SecurityUtil.getCurrentLoginUserId();
                Portfolio myPortfolio = portfolioQueryRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                        new IllegalArgumentException("포트폴리오가 존재하지 않습니다."));
                if (myPortfolio.getId()!=portfolio.getId()) portfolio.increaseViewCount();
            } catch (CustomAuthenticationException e) {
                portfolio.increaseViewCount();
            }

            User user = portfolio.getUser();

            portfolioRepository.save(portfolio);
            return PorfIntroResponseDto.builder()
                    .id(porfId)
                    .title(portfolio.getTitle())
                    .githubUrl(portfolio.getGithubUrl())
                    .blogUrl(portfolio.getBlogUrl())
                    .contents(portfolio.getIntroContents())
                    .bgImage(user.getProfileImgUrl()) // 유저의 프로필 이미지
                    .viewCount(portfolio.getViewCount())
                    .modifiedAt(portfolio.getUpdatedAt().toString())
                    .templateIdx(portfolio.getTemplateIdx())
                    .job(user.getJob())
                    .username(user.getName())
                    .phoneNum(user.getPhoneNum())
                    .email(user.getEmail())
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
            portfolioList = portfolioQueryRepository.findAllFetchUserIfPublicLimit();
        } else {
            //특정 스택을 가진, 공개된 포트폴리오만 조회
            portfolioList = portfolioQueryRepository.findAllByStackNamesIfPublicLimit(stackNames);
        }

        portfolioList.forEach(portfolio -> {
            List<String> stacks = userQueryRepository.findStackNamesByPorfId(portfolio.getId());
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

    public PorfStackReponseDto getStackContents(Integer porfId) {

        boolean myPorf = PorfUtil.isMyPorf(porfId,portfolioQueryRepository);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        if (myPorf || !(portfolio.getIsTemp())) {

            List<String> porfStacks = portfolioQueryRepository.findStackNamesByPorfId(porfId);
            List<String> userStacks = userQueryRepository.findStackNamesByPorfId(porfId);

            return PorfStackReponseDto.builder()
                    .mainStack(userStacks)
                    .subStack(porfStacks)
                    .build();
        } else {
            return null;
        }


    }



    public List<ProjectResponseDto> getProject(Integer porfId) {

        boolean myPorf = PorfUtil.isMyPorf(porfId,portfolioQueryRepository);
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        if (myPorf || !(portfolio.getIsTemp())) {
            List<Project> projects = projectRepository.findAllByPorfId(porfId);
            return ProjectUtil.projectToResponseDtos(projects, projectImgRepository, projectStackRepository);
        } else {
            return null;
        }
    }



    @Transactional
    public void reset() {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
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
