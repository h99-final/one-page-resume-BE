package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;

import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
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
    private final ProjectImgRepository projectImgRepository;
    private final ProjectStackRepository projectStackRepository;

    @Transactional//소개문 작성
    public void createIntro(PorfIntroRequestDto porfIntroRequestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 유저입니다"));

        //유저 이메일로 포폴 아이디 가져오기
        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        portfolio.updateIntro(porfIntroRequestDto.getTitle(), user.getGithubUrl(),
                porfIntroRequestDto.getContents(),
                user.getBlogUrl());

        portfolioRepository.save(portfolio);

    }

    @Transactional //템플릿 테마 지정
    public void updateTemplate(PorfTemplateRequestDto porfTemplateRequestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));


        portfolio.updateTemplate(porfTemplateRequestDto.getIdx());
        portfolioRepository.save(portfolio);


    }


    @Transactional//기술 스택 작성
    public void createStack(StackDto requestDto) {
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        insertStacksInPortFolio(portfolio, requestDto.getStack());
    }

    @Transactional
    public void updateStack(StackDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        //기존에 있는 스택 모두 삭제
        portfolioStackRepository.deleteAllByPorfId(portfolio.getId());

        insertStacksInPortFolio(portfolio, requestDto.getStack());
    }

    @Transactional
    public void createCareer(CareerListRequestDto requestDto) {

        //현재 로그인한 사람
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        requestDto.getCareers().forEach(dto -> {

            List<String> contentsList = dto.getContents();

            String combinedContents = careerContentsListToString(contentsList);

            Career career = Career.create(dto.getTitle(), dto.getSubTitle(), combinedContents,
                    dto.getStartTime(), dto.getEndTime(), portfolio);

            careerRepository.save(career);

        });
    }

    @Transactional
    public void updateCareer(CareerListRequestDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));

        List<Integer> existCareerIds = portfolioRepository.findCareerIdByPorfId(portfolio.getId());

        List<CareerRequestDto> requestDtos = requestDto.getCareers();

        requestDtos.forEach(dto -> {

            Integer careerId = dto.getId();

            //유저가 소유한 포트폴리오에 존재하지 않는 커리어 일때
            if (!existCareerIds.contains(careerId)) {
                //todo: 어떤식으로 에러처리를 해야할까..?
            } else {
                //유저가 소유한 포트폴리오에 존재하는 커리어 일때
                Career career = careerRepository.findById(careerId).orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 커리어입니다."));
                String careerContents = careerContentsListToString(dto.getContents());
                career.updateCareer(dto.getTitle(), dto.getSubTitle(), careerContents, dto.getStartTime(), dto.getEndTime());
                careerRepository.save(career);
            }
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
        Portfolio portfolio = portfolioRepository.findByUserEmail(email).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<Integer> projectIds = requestDto.getProjectId();

        List<Project> projects = projectRepository.findAllByIds(projectIds);

        projects.forEach(project -> project.removePortfolio(portfolio));
    }

    @Transactional
    public PorfIntroResponseDto getIntro(Integer porfId) {

        Integer myPorf = isMyPorf(porfId);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        if (myPorf == 1
                || ((myPorf == 0 || myPorf == -1) && (!portfolio.getIsTemp()))) {
            portfolio.increaseViewCount();
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

        portfolioList.forEach(portfolio -> {
            List<String> stacks = portfolioStackRepository.findStackNamesByPorfId(portfolio.getId());
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

        Integer myPorf = isMyPorf(porfId);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));
        if (myPorf == 1
                || ((myPorf == 0 || myPorf == -1) && (!portfolio.getIsTemp()))) {

            List<String> stackNames = portfolioStackRepository.findStackNamesByPorfId(porfId);

            return StackDto.builder()
                    .stack(stackNames)
                    .build();
        } else {
            return null;
        }


    }

    public CareerListResponseDto getCareer(Integer porfId) {

        Integer myPorf = isMyPorf(porfId);

        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<CareerResponseDto> careerResponseDtos = new ArrayList<>();

        if (myPorf == 1
                || ((myPorf == 0 || myPorf == -1) && (!portfolio.getIsTemp()))) {

            List<Career> careers = careerRepository.findAllByPorfId(porfId);
            careers.forEach(career -> {
                String[] contents = career.getContents().split("----");
                CareerResponseDto responseDto = CareerResponseDto.builder()
                        .id(career.getId())
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

    public ProjectDetailListResponseDto getProject(Integer porfId) {

        Integer myPorf = isMyPorf(porfId);
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                new IllegalArgumentException("포트폴리오가 존재하지 않습니다"));

        List<ProjectDetailResponseDto> projectDetailResponseDtos = new ArrayList<>();

        if (myPorf == 1
                || ((myPorf == 0 || myPorf == -1) && (!portfolio.getIsTemp()))) {

            List<Project> projects = projectRepository.findAllByPorfId(porfId);
            projects.forEach(project -> {
                ProjectImg projectImg = projectImgRepository.findFirstByProjectId(project.getId()).orElse(null);
                String imageUrl = null;
                if (projectImg != null) {
                    imageUrl = projectImg.getImageUrl();
                }

                ProjectDetailResponseDto projectDetailResponseDto = ProjectDetailResponseDto.builder()
                        .title(project.getTitle())
                        .content(project.getIntroduce())
                        .imgUrl(imageUrl)
                        .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                        .build();

                projectDetailResponseDtos.add(projectDetailResponseDto);
            });
        } else {
            return null;
        }

        return ProjectDetailListResponseDto.builder()
                .projects(projectDetailResponseDtos)
                .build();
    }

    @Transactional
    public void updateIntro(PorfIntroRequestDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포트폴리오입니다"));

        portfolio.updateIntro(requestDto.getTitle(), portfolio.getGithubUrl(), requestDto.getContents(), portfolio.getBlogUrl());

        portfolioRepository.save(portfolio);
    }

    @Transactional
    public void reset(Integer porfId) {

        Integer myPorf = isMyPorf(porfId);

        //나의 포트폴리오가 아닐 때
        if (myPorf != 1) {
            throw new IllegalArgumentException("내가 작성한 포트폴리오만 초기화 할 수 있습니다");
        } else {
            Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(() ->
                    new IllegalArgumentException("존재하지 않는 포트폴리오입니다"));

            //기본 정보 리셋 및 비공개 처리
            portfolio.reset();

            //연결된 프로젝트 모두 연결 끊음
            List<Project> projects = projectRepository.findAllByPorfId(porfId);
            projects.forEach(project -> project.removePortfolio(portfolio));
            projectRepository.saveAll(projects);
        }
    }

    private void insertStacksInPortFolio(Portfolio portfolio, List<String> stackNames) {
        //새로 들어온 스택 모두 연결
        stackNames.forEach(name -> {
            Stack stack = stackRepository.findFirstByName(name).orElse(null);
            PortfolioStack createdPortfolioStack = null;
            //이미 존재하는 스택이라면
            if (stack != null) {
                createdPortfolioStack = PortfolioStack.create(portfolio, stack);
            } else {
                //존재하지 않는 스택이라면
                Stack createdStack = Stack.create(name);
                stackRepository.save(createdStack);
                createdPortfolioStack = PortfolioStack.create(portfolio, createdStack);
            }
            portfolioStackRepository.save(createdPortfolioStack);
        });
    }

    private Integer isMyPorf(Integer porfId) {

        try {
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            Portfolio portfolio = portfolioRepository.findByUserEmail(userEmail).orElseThrow(() ->
                    new IllegalArgumentException("존재하지 않는 포트폴리오입니다."));
            if (portfolio.getUser().getId()==(porfId)) {
                return 1;
            } else {
                return 0;
            }

        } catch (CustomAuthenticationException e) {
            return -1;
        }
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
