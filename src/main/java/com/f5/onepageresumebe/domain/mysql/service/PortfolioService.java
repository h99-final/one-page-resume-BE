package com.f5.onepageresumebe.domain.mysql.service;

import com.f5.onepageresumebe.domain.mysql.entity.*;
import com.f5.onepageresumebe.domain.mysql.repository.*;

import com.f5.onepageresumebe.domain.mysql.repository.querydsl.PortfolioQueryRepository;
import com.f5.onepageresumebe.domain.mysql.repository.querydsl.UserQueryRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.web.dto.porf.PorfDto;
import com.f5.onepageresumebe.web.dto.project.ProjectDto;
import com.f5.onepageresumebe.web.dto.stack.StackDto;
import com.f5.onepageresumebe.web.dto.stack.PorfStackReponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
    public void updateIntro(PorfDto.IntroRequest requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포트폴리오입니다"));

        portfolio.updateIntro(requestDto.getTitle(), portfolio.getUser().getGithubUrl(), requestDto.getContents(), portfolio.getUser().getBlogUrl());

        portfolioRepository.save(portfolio);
    }

    @Transactional //템플릿 테마 지정
    public void updateTemplate(PorfDto.TemplateRequest porfTemplateRequestDto) {

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
    public PorfDto.Status changeStatus(PorfDto.Status dto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        boolean changedStatus = portfolio.changeStatus(dto.getShow());

        return PorfDto.Status.builder()
                .show(changedStatus)
                .build();
    }

    @Transactional
    public void inputProjectInPorf(PorfDto.ProjectRequest requestDto) {
        String email = SecurityUtil.getCurrentLoginUserId();
        Portfolio portfolio = portfolioQueryRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        if (projectIds.isEmpty()){
            throw new CustomException("최소 하나의 프로젝트를 선택해 주세요.",INVALID_INPUT_ERROR);
        }

        //기존에 포함되어있던 프로젝트 모두 연결 끊음
        List<Project> existProjects = projectRepository.findAllByPorfId(portfolio.getId());
        existProjects.forEach(project -> project.removePortfolio(portfolio));

        //새로 들어온 프로젝트 모두 연결
        List<Project> projects = projectRepository.findAllByIds(projectIds);

        projects.forEach(project -> {
            if (!project.getUser().getId().equals(portfolio.getUser().getId())) {
                throw new IllegalArgumentException("내가 작성한 프로젝트만 가져올 수 있습니다");
            }
            project.setPortfolio(portfolio);
        });
    }

    @Transactional
    public void deleteProjectInPorf(PorfDto.ProjectRequest requestDto) {
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
    public PorfDto.IntroResponse getIntro(Integer porfId) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        boolean isMyPorf = false;

        Portfolio portfolio = null;

        try {
            portfolio = portfolioQueryRepository.findFirstPorfByPorfIdAndUserEmail(porfId, userEmail).orElseThrow(()->
                    new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
            if(portfolio.getId() == porfId) isMyPorf = true;
        } catch (CustomAuthenticationException e) {
            isMyPorf = false;
        }

        if(portfolio == null)
        {
            portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        }
        if (isMyPorf || !(portfolio.getIsTemp())) {
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
            return PorfDto.IntroResponse.builder()
                    .id(porfId)
                    .title(portfolio.getTitle())
                    .githubUrl(portfolio.getGithubUrl())
                    .blogUrl(portfolio.getBlogUrl())
                    .contents(portfolio.getIntroContents())
                    .profileImage(user.getProfileImgUrl()) // 유저의 프로필 이미지
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
    public List<PorfDto.Response> getIntrosByStacks(StackDto requestDto) {

        List<String> stackNames = requestDto.getStack();
        List<PorfDto.Response> responseDtoList = new ArrayList<>();
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
            User user = portfolio.getUser();
            List<String> stacks = userQueryRepository.findStackNamesByPorfId(portfolio.getId());
            PorfDto.Response responseDto = PorfDto.Response.builder()
                    .porfId(portfolio.getId())
                    .username(user.getName())
                    .userStack(stacks)
                    .title(portfolio.getTitle())
                    .templateIdx(portfolio.getTemplateIdx())
                    .job(user.getJob())
                    .build();
            responseDtoList.add(responseDto);
        });

        return responseDtoList;
    }

    public PorfStackReponseDto getStackContents(Integer porfId) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        boolean isMyPorf = false;

        Portfolio portfolio = null;

        try {
            portfolio = portfolioQueryRepository.findFirstPorfByPorfIdAndUserEmail(porfId, userEmail).orElseThrow(()->
                    new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
            if(portfolio.getId() == porfId) isMyPorf = true;
        } catch (CustomAuthenticationException e) {
            isMyPorf = false;
        }

        if(portfolio == null)
        {
            portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        }
        if (isMyPorf || !(portfolio.getIsTemp())) {

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

    public List<ProjectDto.Response> getProject(Integer porfId) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        boolean isMyPorf = false;

        Portfolio portfolio = null;

        try {
            portfolio = portfolioQueryRepository.findFirstPorfByPorfIdAndUserEmail(porfId, userEmail).orElseThrow(()->
                    new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
            if(portfolio.getId() == porfId) isMyPorf = true;
        } catch (CustomAuthenticationException e) {
            isMyPorf = false;
        }

        if(portfolio == null)
        {
            portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        }

        if (isMyPorf || !(portfolio.getIsTemp())) {
            List<Project> projects = projectRepository.findAllByPorfId(porfId);
            HashMap<Integer, List<String>> stackMap = new HashMap<>();
            HashMap<Integer, ProjectImg> imageMap = new HashMap<>();

            for(Project project : projects) {
                Integer projectId = project.getId();

                stackMap.put(projectId,projectStackRepository.findStackNamesByProjectId(projectId));
                ProjectImg projectImg = projectImgRepository.findFirstByProjectId(projectId).orElse(null);
                if(projectImg != null) {
                    imageMap.put(projectId, projectImg);}
                else {
                    imageMap.put(projectId, null);
                }
            }

            return ProjectUtil.projectToResponseDtos(projects, imageMap, stackMap);
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
            Stack stack = stackRepository.findFirstByName(name).orElse(null);
            if (stack == null) {
                stack = Stack.create(name);
                stackRepository.save(stack);
            }
            PortfolioStack portfolioStack = PortfolioStack.create(portfolio, stack);
            portfolioStackRepository.save(portfolioStack);
        });
    }

}
