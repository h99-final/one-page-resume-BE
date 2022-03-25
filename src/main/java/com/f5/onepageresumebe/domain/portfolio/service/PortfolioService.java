package com.f5.onepageresumebe.domain.portfolio.service;

import com.f5.onepageresumebe.domain.career.repository.CareerRepository;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.entity.PortfolioStack;
import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import com.f5.onepageresumebe.domain.stack.entity.Stack;
import com.f5.onepageresumebe.domain.stack.repository.StackRepository;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepository;
import com.f5.onepageresumebe.domain.portfolio.repository.PortfolioStackRepository;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.project.repository.ProjectImgRepository;
import com.f5.onepageresumebe.domain.project.repository.project.ProjectRepository;
import com.f5.onepageresumebe.domain.project.repository.ProjectStackRepository;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.domain.user.repository.stack.UserStackRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.util.UserUtil;
import com.f5.onepageresumebe.web.portfolio.dto.PorfDto;
import com.f5.onepageresumebe.web.project.dto.ProjectDto;
import com.f5.onepageresumebe.web.stack.dto.StackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;
import static com.f5.onepageresumebe.exception.ErrorCode.NOT_EXIST_ERROR;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final UserStackRepository userStackRepository;

    private final PortfolioRepository portfolioRepository;
    private final PortfolioStackRepository portfolioStackRepository;

    private final StackRepository stackRepository;

    private final CareerRepository careerRepository;

    private final ProjectRepository projectRepository;
    private final ProjectImgRepository projectImgRepository;
    private final ProjectStackRepository projectStackRepository;

    @Transactional
    public void updateIntro(PorfDto.IntroRequest requestDto) {

        //현재 로그인한 유저 확인
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //로그인한 유저의 포트폴리오 가져옴
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new CustomException("존재하지 않는 포트폴리오입니다",NOT_EXIST_ERROR));

        //업데이트
        portfolio.updateIntro(requestDto.getTitle(), portfolio.getUser().getGithubUrl(), requestDto.getContents(), portfolio.getUser().getBlogUrl());

        portfolioRepository.save(portfolio);
    }

    @Transactional
    public void updateTemplate(PorfDto.TemplateRequest porfTemplateRequestDto) {

        //현재 로그인한 유저 확인
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //로그인한 유저의 포트폴리오 가져옴
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new CustomException("포트폴리오가 존재하지 않습니다",NOT_EXIST_ERROR));

        //업데이트
        portfolio.updateTemplate(porfTemplateRequestDto.getIdx());

        portfolioRepository.save(portfolio);
    }


    @Transactional
    public void updateStack(StackDto requestDto) {

        //현재 로그인한 유저 확인
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //로그인한 유저의 포트폴리오 가져옴
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(
                () -> new CustomException("포트폴리오가 존재하지 않습니다",NOT_EXIST_ERROR));

        List<String> stacks = requestDto.getStack();

        //스택 갯수 확인
        checkStackSize(stacks);

        //기존에 있는 스택 모두 삭제
        portfolioStackRepository.deleteAllByPorfId(portfolio.getId());

        //포트폴리오에 스택 삽입
        insertStacksInPortfolio(portfolio, stacks);
    }

    @Transactional
    public PorfDto.Status changeStatus(PorfDto.Status dto) {

        //현재 로그인한 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //로그인한 유저의 포트폴리오 가져옴
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new CustomException("포트폴리오가 존재하지 않습니다",NOT_EXIST_ERROR));

        //포트폴리오 상태 변경
        boolean changedStatus = portfolio.changeStatus(dto.getShow());

        //변경된 상태값 리턴
        return PorfDto.Status.builder()
                .show(changedStatus)
                .build();
    }

    @Transactional
    public void inputProjectInPorf(PorfDto.ProjectRequest requestDto) {

        //현재 로그인한 유저
        String email = SecurityUtil.getCurrentLoginUserId();

        //로그인한 유저의 포트폴리오
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(email).orElseThrow(() ->
                new CustomException("포트폴리오가 존재하지 않습니다",NOT_EXIST_ERROR));

        List<Integer> projectIds = requestDto.getProjectId();

        //빈 리스트인지 확인
        checkProjectIdsEmpty(projectIds);

        //기존에 포함되어있던 프로젝트 모두 연결 끊음
        List<Project> existProjects = projectRepository.findAllByPorfId(portfolio.getId());
        existProjects.forEach(project -> project.removePortfolio(portfolio));

        //새로 들어온 프로젝트 모두 연결
        projectRepository.findAllByIds(projectIds).forEach(project -> {
            if (!project.getUser().getId().equals(portfolio.getUser().getId())) {
                throw new CustomException("내가 작성한 프로젝트만 가져올 수 있습니다",INVALID_INPUT_ERROR);
            }
            project.setPortfolio(portfolio);
        });
    }

    @Transactional
    public PorfDto.IntroResponse getIntro(Integer porfId) {

        //내 포트폴리오인지 확인
        boolean isMyPorf = isMyPorf(porfId);

        //조회하고자 하는 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new CustomException("포트폴리오가 존재하지 않습니다",NOT_EXIST_ERROR));

        //나의 포트폴리오거나 포트폴리오가 공개된 상태일때
        if (isMyPorf || !(portfolio.getIsTemp())) {
            try {
                //로그인한 유저
                String email = SecurityUtil.getCurrentLoginUserId();

                //현재 조회하고자하는 포트폴리오가 로그인한 유저의 포트폴리오인지 확인
                boolean exists = portfolioRepository.existsByUserEmailAndPorfId(email, porfId);

                //로그인한 유저의 포트폴리오가 아니라면
                if(!exists){
                    portfolio.increaseViewCount();
                }

            } catch (CustomAuthenticationException e) {
                //비로그인일때
                portfolio.increaseViewCount();
            }

            //포트폴리오의 주인
            User user = portfolio.getUser();

            //조회수 늘린 것을 반환하기 위해 미리 저장
            portfolioRepository.save(portfolio);

            return PorfDto.IntroResponse.builder()
                    .id(porfId)
                    .title(portfolio.getTitle())
                    .githubUrl(portfolio.getGithubUrl())
                    .blogUrl(portfolio.getBlogUrl())
                    .contents(portfolio.getIntroContents())
                    .profileImage(user.getProfileImgUrl())
                    .viewCount(portfolio.getViewCount())
                    .modifiedAt(portfolio.getUpdatedAt().toString())
                    .templateIdx(portfolio.getTemplateIdx())
                    .job(user.getJob())
                    .username(user.getName())
                    .phoneNum(user.getPhoneNum())
                    .email(UserUtil.convertUserEmail(user.getEmail(),user.getIsKakao()))
                    .build();
        } else {
            //비공개이고, 내 포트폴리오가 아닐 때
            return null;
        }


    }

    //전체 조회
    public List<PorfDto.Response> getIntrosByStacks(StackDto requestDto) {

        //조회에 사용될 스택들
        List<String> stackNames = requestDto.getStack();

        //ResponseDto를 담을 리스트
        List<PorfDto.Response> responseDtoList = new ArrayList<>();

        //조회될 포트폴리오들
        List<Portfolio> portfolioList;

        if (stackNames.size() == 0) {
            //특정 조건이 없을 때
            //공개 된 것들만 가져온다
            portfolioList = portfolioRepository.findAllFetchUserIfPublicLimit();
        } else {
            //특정 스택을 가진, 공개된 포트폴리오만 조회
            portfolioList = portfolioRepository.findAllByStackNamesIfPublicLimit(stackNames);
        }

        //Dto 변환
        portfolioList.forEach(portfolio -> {

            responseDtoList.add(portfolioToResponseDto(portfolio));
        });

        return responseDtoList;
    }

    public PorfDto.StackResponse getStackContents(Integer porfId) {

        //로그인한 유저의 포트폴리오인지 확인
        boolean isMyPorf = isMyPorf(porfId);

        //조회하고자하는 포트폴리오
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new CustomException("존재하지 않는 포트폴리오입니다.",NOT_EXIST_ERROR));

        //로그인한 유저의 포트폴리오거나, 공개상태의 포트폴리오일때
        if (isMyPorf || !(portfolio.getIsTemp())) {

            //포트폴리오 스택과 유저 스택 조회
            List<String> porfStacks = portfolioRepository.findStackNamesByPorfId(porfId);
            List<String> userStacks = userStackRepository.findStackNamesByPorfId(porfId);

            return PorfDto.StackResponse.builder()
                    .mainStack(userStacks)
                    .subStack(porfStacks)
                    .build();
        } else {
            //비공개상태이고 나의 포트폴리오가 아닐 때
            return null;
        }

    }

    public List<ProjectDto.Response> getProject(Integer porfId) {

        //로그인한 유저의 포트폴리오인지 확인
        boolean isMyPorf = isMyPorf(porfId);

        //조회하고자 하는 포트폴리오
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new CustomException("존재하지 않는 포트폴리오입니다.",NOT_EXIST_ERROR));

        //로그인한 유저의 포트폴리오거나, 공개상태의 포트폴리오일때
        if (isMyPorf || !(portfolio.getIsTemp())) {

            //Util class에서 전달해줄 map
            HashMap<Integer, List<String>> stackMap = new HashMap<>();
            HashMap<Integer, ProjectImg> imageMap = new HashMap<>();

            //포트폴리오의 프로젝트들
            List<Project> projects = projectRepository.findAllByPorfId(porfId);

            //Util class에 넘겨줄 정보 세팅
            projects.forEach(project -> {
                Integer projectId = project.getId();

                stackMap.put(projectId,projectStackRepository.findStackNamesByProjectId(projectId));

                ProjectImg projectImg = projectImgRepository.findFirstByProjectId(projectId).orElse(null);

                if(projectImg != null) {
                    imageMap.put(projectId, projectImg);}
                else {
                    imageMap.put(projectId, null);
                }
            });

            return ProjectUtil.projectToResponseDtos(projects, imageMap, stackMap);
        } else {
            //비공개상태이고 나의 포트폴리오가 아닐 때
            return null;
        }
    }

    @Transactional
    public void reset() {

        //현재 로그인한 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //초기화하고자 하는 포트폴리오
        Portfolio portfolio = portfolioRepository.findByUserEmailFetchUser(userEmail).orElseThrow(() ->
                new CustomException("존재하지 않는 포트폴리오입니다.",NOT_EXIST_ERROR));

        Integer porfId = portfolio.getId();

        //연결된 프로젝트 모두 연결 끊기
        List<Project> projects = projectRepository.findAllByPorfId(porfId);
        projects.forEach(project -> project.removePortfolio(portfolio));

        //연결된 커리어 모두 삭제
        careerRepository.deleteAllByPorfId(porfId);

        //연결된 기술 스택 모두 연결 끊기
        portfolioStackRepository.deleteAllByPorfId(porfId);

        //내용 초기화
        portfolio.reset();

    }

    private void insertStacksInPortfolio(Portfolio portfolio, List<String> stackNames) {

        //중복 스택 입력시, 중복데이터 제거
        stackNames = stackNames.stream().distinct().collect(Collectors.toList());

        stackNames.forEach(name -> {
            Stack stack = stackRepository.findFirstByName(name).orElse(null);
            //존재하지 않는 스택이면 새로 생성하여 저장
            if (stack == null) {
                stack = Stack.create(name);
                stackRepository.save(stack);
            }
            //스택과 포트폴리오 연결
            PortfolioStack portfolioStack = PortfolioStack.create(portfolio, stack);
            portfolioStackRepository.save(portfolioStack);
        });
    }

    private void checkStackSize(List<String> stacks){

        if (stacks.size()<3){
            throw new CustomException("포트폴리오 스택을 3개 이상 선택해 주세요.",INVALID_INPUT_ERROR);
        }

    }

    private void checkProjectIdsEmpty(List<Integer> projectIds){

        if(projectIds.isEmpty()){
            throw new CustomException("최소 하나의 프로젝트를 선택해 주세요.",INVALID_INPUT_ERROR);
        }
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

    private PorfDto.Response portfolioToResponseDto(Portfolio portfolio){

        //포트폴리오 주인
        User user = portfolio.getUser();

        //유저의 스택
        List<String> stacks = userStackRepository.findStackNamesByPorfId(portfolio.getId());
        return PorfDto.Response.builder()
                .porfId(portfolio.getId())
                .username(user.getName())
                .userStack(stacks)
                .title(portfolio.getTitle())
                .templateIdx(portfolio.getTemplateIdx())
                .job(user.getJob())
                .build();
    }

}
