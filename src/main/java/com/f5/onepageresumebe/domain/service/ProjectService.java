package com.f5.onepageresumebe.domain.service;


import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;
import com.f5.onepageresumebe.domain.repository.querydsl.ProjectQueryRepository;
import com.f5.onepageresumebe.domain.repository.querydsl.UserQueryRepository;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.GitUtil;
import com.f5.onepageresumebe.util.ProjectUtil;
import com.f5.onepageresumebe.util.StackUtil;
import com.f5.onepageresumebe.web.dto.gitFile.responseDto.TroubleShootingFileResponseDto;
import com.f5.onepageresumebe.web.dto.project.requestDto.ProjectUpdateRequestDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import com.f5.onepageresumebe.web.dto.project.requestDto.ProjectRequestDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.TroubleShootingsResponseDto;
import com.f5.onepageresumebe.web.dto.stack.StackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;

@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final StackRepository stackRepository;
    private final ProjectStackRepository projectStackRepository;
    private final S3Uploader s3Uploader;
    private final ProjectImgRepository projectImgRepository;
    private final UserRepository userRepository;
    private final GitCommitRepository gitCommitRepository;
    private final GitFileRepository gitFileRepository;
    private final ProjectQueryRepository projectQueryRepository;
    private final UserQueryRepository userQueryRepository;


    @Transactional//프로젝트 생성
    public ProjectResponseDto createProject(ProjectRequestDto requestDto, List<MultipartFile> multipartFiles) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(userEmail).orElseThrow(()->
                new IllegalArgumentException("유저 정보가 존재하지 않습니다."));

        Project project = Project.create(requestDto.getTitle(), requestDto.getContent(),
                requestDto.getGitRepoName(), requestDto.getGitRepoUrl(), user);

        projectRepository.save(project);

        List<String> stacks = requestDto.getStack();

        if(stacks.size()<3){
            throw new CustomException("프로젝트 기술 스택을 3가지 이상 선택해 주세요.", INVALID_INPUT_ERROR);
        }else if(multipartFiles.isEmpty()){
            throw new CustomException("프로젝트 이미지를 1개 이상 업로드 해주세요.", INVALID_INPUT_ERROR);
        }

        //스택 넣기
        insertStacksInProject(project, stacks);

        //이미지 넣기
        addImages(project,multipartFiles);

        Integer projectId = project.getId();

        return ProjectResponseDto.builder()
                .id(projectId)
                .title(project.getTitle())
                .imageUrl(projectImgRepository.findFirstByProjectId(projectId).get()
                        .getImageUrl())
                .stack(stacks)
                .build();
    }

    @Transactional
    public void updateProjectImages(Integer projectId,List<MultipartFile> multipartFiles){

        Project project = getProjectIfMyProject(projectId);

        if(project==null){
            throw new IllegalArgumentException("내가 작성한 프로젝트만 수정할 수 있습니다.");
        }

        //연결되어 있는 모든 사진들 삭제
        List<ProjectImg> projectImgs = projectImgRepository.findAllByProjectId(projectId);
      
        s3Uploader.deleteProjectImages(projectImgs);

        projectImgRepository.deleteAllInBatch(projectImgs);

        //새로운 사진 모두 추가
        addImages(project,multipartFiles);

    }

    @Transactional
    public void updateProjectInfo(Integer projectId,ProjectUpdateRequestDto requestDto){

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        User user = userQueryRepository.findByEmail(userEmail).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 유저입니다."));

        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        // !project.getUser.getId().equals(user.getID())
        if(!(project.getUser().getId().equals(user.getId()))){
            throw new IllegalArgumentException("내가 작성한 프로젝트만 수정할 수 있습니다");
        }

        List<String> stacks = requestDto.getStack();

        if (stacks.size()<3){
            throw new CustomException("프로젝트 스택을 3개 이상 선택해 주세요.",INVALID_INPUT_ERROR);
        }

        project.updateIntro(requestDto);

        //기존에 있던 모든 연결된 스택 제거
        projectStackRepository.deleteAllByProjectId(projectId);

        //새로 들어온 스택 모두 프로젝트와 연결
        insertStacksInProject(project, stacks);
    }

    public List<ProjectResponseDto> getShortInfos(){

        String email = SecurityUtil.getCurrentLoginUserId();

        List<Project> projects = projectQueryRepository.findAllByUserEmail(email);

        return ProjectUtil.projectToResponseDtos(projects, projectImgRepository, projectStackRepository);
    }

    public List<ProjectResponseDto> getAllByStacks(StackDto requestDto){

        List<String> stackNames = requestDto.getStack();

        List<Project> projects = projectQueryRepository.findAllByStackNames(stackNames);

        Collections.shuffle(projects);

        return ProjectUtil.projectToResponseDtos(projects, projectImgRepository, projectStackRepository);

    }

    public Project getProjectIfMyProject(Integer projectId) {

        String email = SecurityUtil.getCurrentLoginUserId();
        Project project = projectQueryRepository.findByUserEmailAndProjectId(email, projectId).orElse(null);
//        List<Project> projects = projectRepository.findAllByUserEmail(email);
//        Project project = null;
//
//        for(Project curProject : projects) {
//            if(curProject.getId().equals( projectId)) project = curProject;
//        }

        return project;
    }

    public List<TroubleShootingsResponseDto> getTroubleShootings(Integer projectId) {
        List<TroubleShootingsResponseDto> troubleShootingsResponseDtos = new ArrayList<>();

        Project project = projectRepository.getById(projectId);

        List<GitCommit> gitCommitList = project.getGitCommitList();

        //프로젝트가 가지고 있는 커밋(저장된 커밋)
        for(GitCommit curGitCommit : gitCommitList) {

            Integer commitId = curGitCommit.getId();
            String tsName = curGitCommit.getTsName();

            //커밋이 가지고있는 파일들
            List<GitFile> gitFileList = curGitCommit.getFileList();
            //파일 정보들을 저장할 공간
            List<TroubleShootingFileResponseDto> tsFiles = new ArrayList<>();
            for(GitFile curGitFile : gitFileList) {
                List<String> tsPatchCodes = GitUtil.parsePatchCode(curGitFile.getPatchCode());
                Integer fileId = curGitFile.getId();
                String fileName = curGitFile.getName();
                String tsContent = curGitFile.getTroubleContents();

                TroubleShootingFileResponseDto fileDto = new TroubleShootingFileResponseDto(fileId, fileName,tsContent,tsPatchCodes);
                //리스트에 각각의 file 추가
                tsFiles.add(fileDto);
            }
            //각각의 커밋데이터 추가
            TroubleShootingsResponseDto curDto = new TroubleShootingsResponseDto(commitId,tsName, tsFiles);
            //curDto에 데이터 넣고, 리스트 clear
            tsFiles.clear();

            troubleShootingsResponseDtos.add(curDto);
        }

        return troubleShootingsResponseDtos;
  }

    private void insertStacksInProject(Project project,List<String> stackNames){
        //중복 스택 입력시, 중복데이터 제거
        stackNames = stackNames.stream().distinct().collect(Collectors.toList());

        stackNames.forEach(name-> {
            Stack stack = StackUtil.createStack(name, stackRepository);
            ProjectStack projectStack = ProjectStack.create(project, stack);
            projectStackRepository.save(projectStack);
        });
    }

    private void addImages(Project project,List<MultipartFile> multipartFiles){

        multipartFiles.forEach(multipartFile -> {
            try{
                String projectImgUrl = s3Uploader.upload(multipartFile, "project/" + project.getTitle());
                ProjectImg projectImg = ProjectImg.create(project, projectImgUrl);
                projectImgRepository.save(projectImg);
            }catch (IOException e){
                log.error("createProject -> imageUpload : {}",e.getMessage());
                throw new IllegalArgumentException("사진 업로드에 실패하였습니다.");
            }
        });

        projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Integer projectId) {

        Project project = getProjectIfMyProject(projectId);

        if(project ==null){
            throw new IllegalArgumentException("내가 작성한 프로젝트만 삭제할 수 있습니다");
        }

        List<GitCommit> gitCommitList = gitCommitRepository.findAllByProjectId(projectId);

        //프로젝트에 연결된 커밋들 삭제
        for(GitCommit curCommit : gitCommitList) {
            deleteProjectTroubleShootings(projectId, curCommit.getId());
        }
        //프로젝트의 스택들 전부 삭제
        projectStackRepository.deleteAllByProjectId(projectId);

//        //연결되어 있는 모든 사진들 삭제
        List<ProjectImg> projectImgs = projectImgRepository.findAllByProjectId(projectId);
        s3Uploader.deleteProjectImages(projectImgs);
        projectImgRepository.deleteAllInBatch(projectImgs);

        projectRepository.deleteById(projectId);

    }

    @Transactional
    public void deleteProjectTroubleShootings(Integer projectId, Integer commitId) {

        Project project = getProjectIfMyProject(projectId);

        if(project == null) throw new IllegalArgumentException("프로젝트가 없거나, 프로젝트 주인이 아닙니다.");

        GitCommit gitCommit = gitCommitRepository.getById(commitId);

        List<GitFile> gitFileList = gitCommit.getFileList();
        gitFileRepository.deleteAllInBatch(gitFileList);

        gitCommitRepository.deleteById(commitId);
    }
}
