package com.f5.onepageresumebe.domain.project.service;


import com.f5.onepageresumebe.domain.common.check.CheckOwnerService;
import com.f5.onepageresumebe.domain.project.repository.project.ProjectRepository;
import com.f5.onepageresumebe.domain.user.repository.UserRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
import com.f5.onepageresumebe.util.S3Uploader;
import com.f5.onepageresumebe.domain.git.entity.GitCommit;
import com.f5.onepageresumebe.domain.git.entity.GitFile;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.project.entity.ProjectBookmark;
import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import com.f5.onepageresumebe.domain.project.entity.ProjectStack;
import com.f5.onepageresumebe.domain.project.repository.*;
import com.f5.onepageresumebe.domain.stack.entity.Stack;
import com.f5.onepageresumebe.domain.stack.repository.StackRepository;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.GitUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.web.git.dto.FileDto;
import com.f5.onepageresumebe.web.project.dto.ProjectDto;
import com.f5.onepageresumebe.web.stack.dto.StackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.f5.onepageresumebe.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {

    private final CheckOwnerService checkOwnerService;

    private final S3Uploader s3Uploader;

    private final ProjectRepository projectRepository;
    private final ProjectStackRepository projectStackRepository;
    private final ProjectImgRepository projectImgRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;

    private final StackRepository stackRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectDto.Response createProject(ProjectDto.Request requestDto, List<MultipartFile> multipartFiles) {

        //현재 로그인한 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //프로젝트 생성 및 유저와 연결
        Project project = Project.create(requestDto.getTitle(), requestDto.getContent(),
                requestDto.getGitRepoName(), requestDto.getGitRepoUrl(), user);

        //프로젝트 저장
        projectRepository.save(project);

        List<String> stacks = requestDto.getStack();

        //검증
        checkStacks(stacks);

        //스택 넣기
        insertStacksInProject(project, stacks);

        //이미지 넣기
        addImages(project, multipartFiles);

        return ProjectDto.Response.builder()
                .id(project.getId())
                .build();
    }

    @Transactional
    public void updateProjectImages(Integer projectId, List<MultipartFile> multipartFiles) {

        //나의 프로젝트일때만 가져오기
        boolean isMyProject = checkOwnerService.isMyProject(projectId);

        if (isMyProject) {
            Project project = projectRepository.findById(projectId).orElseThrow(() ->
                    new CustomException("존재하지 않는 프로젝트입니다", NOT_EXIST_ERROR));

            //새로운 사진 모두 추가
            addImages(project, multipartFiles);
        } else {
            throw new CustomAuthorizationException("내가 작성한 프로젝트만 수정할 수 있습니다.");
        }

    }

    @Transactional
    public void updateProjectInfo(Integer projectId, ProjectDto.Request requestDto) {

        //나의 프로젝트일때만 가져오기
        boolean isMyProject = checkOwnerService.isMyProject(projectId);

        if (isMyProject) {
            Project project = projectRepository.findById(projectId).orElseThrow(() ->
                    new CustomException("존재하지 않는 프로젝트입니다", NOT_EXIST_ERROR));

            List<String> stacks = requestDto.getStack();

            //스택 검증
            checkStacks(stacks);

            //업데이트
            project.updateIntro(requestDto);

            //기존에 있던 모든 연결된 스택 제거
            projectStackRepository.deleteAllByProjectId(projectId);

            //새로 들어온 스택 모두 프로젝트와 연결
            insertStacksInProject(project, stacks);
        } else {
            throw new CustomAuthorizationException("내가 작성한 프로젝트만 수정할 수 있습니다.");
        }


    }

    public List<ProjectDto.Response> getShortInfos() {

        //현재 로그인 유저
        String email = SecurityUtil.getCurrentLoginUserId();

        //로그인 유저의 프로젝트들
        List<Project> projects = projectRepository.findAllByUserEmail(email);

        //util class에서 사용할 정보들을 담은 map
        HashMap<Integer, List<String>> stackMap = new HashMap<>();
        HashMap<Integer, ProjectImg> imageMap = new HashMap<>();

        //dto로 변환
        projectRepository.findAllByUserEmail(email).forEach(project -> {

            Integer projectId = project.getId();

            stackMap.put(projectId, projectStackRepository.findStackNamesByProjectId(projectId));

            ProjectImg projectImg = projectImgRepository.findFirstByProjectId(projectId).orElse(null);

            imageMap.put(projectId, projectImg);
        });

        return ProjectUtil.projectToResponseDtos(projects, imageMap, stackMap);
    }

    public List<ProjectDto.Response> getAllByStacks(StackDto requestDto, Pageable pageable) {

        //현재 유저의 프로젝트 id들
        Set<Integer> myProjectIds = new HashSet<>();
        //현재 유저가 북마크한 프로젝트 id들
        Set<Integer> bookmarkingProjectIds = new HashSet<>();

        try {
            //현재 로그인 유저
            String userEmail = SecurityUtil.getCurrentLoginUserId();

            //현재 로그인한 유저가 소유한 프로젝트들
            myProjectIds = projectRepository.findProjectIdsByUserEmail(userEmail);

            //현재 로그인한 유저가 북마크한 프로젝트 Id들
            bookmarkingProjectIds = projectBookmarkRepository.findByUserEmail(userEmail);

        } catch (CustomAuthenticationException e) {

        }


        List<String> stackNames = requestDto.getStack();

        List<Project> projects;

        if (stackNames.size() == 0) {
            //조회 스택을 선택하지 않았다면 전체 조회
            projects = projectRepository.findAllByOrderByBookmarkCountDescPaging(pageable).getContent();
        } else {
            //조회 스택을 선택했다면 해당 스택을 가진 프로젝트만 조회
            projects = projectRepository.findAllByStackNames(stackNames, pageable);
        }

        //util class에서 사용할 정보들을 담은 map
        HashMap<Integer, List<String>> stackMap = new HashMap<>();
        HashMap<Integer, ProjectImg> imageMap = new HashMap<>();

        //dto로 변환
        projects.forEach(project -> {
            Integer projectId = project.getId();

            stackMap.put(projectId, projectStackRepository.findStackNamesByProjectId(projectId));
            ProjectImg projectImg = projectImgRepository.findFirstByProjectId(projectId).orElse(null);
            imageMap.put(projectId, projectImg);
        });

        return ProjectUtil.projectToResponseDtos(projects, imageMap, stackMap, myProjectIds, bookmarkingProjectIds);
    }

    public List<ProjectDto.TroubleShootingsResponse> getTroubleShootings(Integer projectId) {

        List<ProjectDto.TroubleShootingsResponse> troubleShootingsResponseDtos = new ArrayList<>();

        Project project = projectRepository.getById(projectId);

        List<GitCommit> gitCommitList = project.getGitCommitList();

        //프로젝트가 가지고 있는 커밋(저장된 커밋)
        for (GitCommit curGitCommit : gitCommitList) {

            Integer commitId = curGitCommit.getId();
            String tsName = curGitCommit.getTsName();
            String sha = curGitCommit.getSha();
            String commitMsg = curGitCommit.getMessage();

            //커밋이 가지고있는 파일들
            List<GitFile> gitFileList = curGitCommit.getFileList();
            //파일 정보들을 저장할 공간
            List<FileDto.TroubleShooting> tsFiles = new ArrayList<>();
            for (GitFile curGitFile : gitFileList) {
                List<String> tsPatchCodes = GitUtil.parsePatchCode(curGitFile.getPatchCode());
                Integer fileId = curGitFile.getId();
                String fileName = curGitFile.getName();
                String tsContent = curGitFile.getTroubleContents();

                FileDto.TroubleShooting fileDto = FileDto.TroubleShooting.builder()
                        .fileId(fileId)
                        .fileName(fileName)
                        .tsContent(tsContent)
                        .tsPatchCodes(tsPatchCodes)
                        .build();
                //리스트에 각각의 file 추가
                tsFiles.add(fileDto);
            }
            //각각의 커밋데이터 추가
            ProjectDto.TroubleShootingsResponse curDto = ProjectDto.TroubleShootingsResponse.builder()
                    .commitId(commitId)
                    .sha(sha)
                    .commitMsg(commitMsg)
                    .tsName(tsName)
                    .tsFiles(new ArrayList<>(tsFiles))
                    .build();
            //curDto에 데이터 넣고, 리스트 clear
            tsFiles.clear();

            troubleShootingsResponseDtos.add(curDto);
        }

        return troubleShootingsResponseDtos;
    }

    private void insertStacksInProject(Project project, List<String> stackNames) {
        //중복 스택 입력시, 중복데이터 제거
        stackNames = stackNames.stream().distinct().collect(Collectors.toList());

        stackNames.forEach(name -> {
            Stack stack = stackRepository.findFirstByName(name).orElse(null);
            //존재하지 않는 스택일경우 새로 생성
            if (stack == null) {
                stack = Stack.create(name);
                stackRepository.save(stack);
            }
            //프로젝트와 스택 연결
            ProjectStack projectStack = ProjectStack.create(project, stack);

            projectStackRepository.save(projectStack);
        });
    }

    public ProjectDto.DetailResponse getProjectDetail(Integer projectId) {

        Project project;
        boolean isMyProject = true;
        boolean isBookmarking = false;

        try {
            //현재 로그인한 유저
            String userEmail = SecurityUtil.getCurrentLoginUserId();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

            //프로젝트 주인이 조회하는지 체크
            project = projectRepository.findByUserEmailAndProjectId(userEmail, projectId).orElse(null);

            //본 주인이 아니라면, isMyProject : false, 프로젝트 가져오기
            if (project == null) {
                isMyProject = false;
                project = projectRepository.getById(projectId);

                Optional<ProjectBookmark> optionalProjectBookmark = projectBookmarkRepository.findFirstByUserIdAndProjectId(user.getId(), projectId);
                isBookmarking = optionalProjectBookmark.isPresent();
            }
        } catch (CustomAuthenticationException e) {
            //비로그인
            project = projectRepository.getById(projectId);
            isMyProject = false;
            isBookmarking = false;
        }

        ProjectDto.DetailResponse projectDetailResponseDto = projectToDetailResponseDto(project);

        projectDetailResponseDto.checkBookmark(isMyProject, isBookmarking);

        return projectDetailResponseDto;
    }


    public void addImages(Project project, List<MultipartFile> multipartFiles) {


        multipartFiles.forEach(multipartFile -> {

            //s3에 업로드
            String projectImgUrl = s3Uploader.uploadS3Ob(multipartFile, "project/" + project.getTitle());

            //projectImg 생성 후 프로젝트와 연결, 저장
            ProjectImg projectImg = ProjectImg.create(project, projectImgUrl);
            projectImgRepository.save(projectImg);

        });

    }


    private ProjectDto.DetailResponse projectToDetailResponseDto(Project project) {

        List<ProjectImg> projectImgs = projectImgRepository.findAllByProjectId(project.getId());

        User user = project.getUser();

        ProjectDto.DetailResponse projectDetailResponseDto = ProjectDto.DetailResponse.builder()
                .title(project.getTitle())
                .content(project.getIntroduce())
                .img(projectImgs.stream().map(ProjectImg::toProjectImgResponseDto).collect(Collectors.toList()))
                .bookmarkCount(project.getBookmarkCount())
                .stack(projectStackRepository.findStackNamesByProjectId(project.getId()))
                .userJob(user.getJob())
                .username(user.getName())
                .gitRepoUrl(project.getGitRepoUrl())
                .build();

        return projectDetailResponseDto;
    }

    private void checkStacks(List<String> stacks) {
        if (stacks.size() < 3) {
            throw new CustomException("프로젝트 기술 스택을 3가지 이상 선택해 주세요.", INVALID_INPUT_ERROR);
        }
    }
}