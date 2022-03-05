package com.f5.onepageresumebe.domain.service;


import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.GitPatchCodeUtil;
import com.f5.onepageresumebe.web.dto.gitFile.responseDto.TroubleShootingFileResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import com.f5.onepageresumebe.web.dto.project.requestDto.CreateProjectRequestDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectShortInfoResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.TroubleShootingsResponseDto;
import javafx.beans.property.ListProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final StackRepository stackRepository;
    private final ProjectStackRepository projectStackRepository;
    private  final S3Uploader s3Uploader;
    private final ProjectImgRepository projectImgRepository;
    private final UserRepository userRepository;
    private final GitPatchCodeUtil gitPatchCodeUtil;


    @Transactional//프로젝트 생성
    public ProjectResponseDto createProject(CreateProjectRequestDto requestDto, List<MultipartFile> multipartFiles) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail).get();

        Project project = Project.create(requestDto.getProjectTitle(), requestDto.getProjectContent(),
                requestDto.getGitRepoName(), requestDto.getGitRepoUrl(), user);

        projectRepository.save(project);

        List<String> stackNames = requestDto.getProjectStack();

        //스택 넣기
        stackNames.stream().forEach(name->{
            Stack stack = stackRepository.findFirstByName(name).orElse(null);

            //스택이 이미 존재할 때
            if (stack!=null){
                ProjectStack projectStack = projectStackRepository.findFirstByProjectAndStack(project, stack).orElse(null);
                //연결되어 있지 않을 때
                if(projectStack==null){
                    ProjectStack createdProjectStack = ProjectStack.create(project, stack);
                    projectStackRepository.save(createdProjectStack);
                }
            }else{
                //스택이 존재하지 않을 때
                Stack createdStack = Stack.create(name);
                stackRepository.save(createdStack);
                ProjectStack createdProjectStack = ProjectStack.create(project, createdStack);
                projectStackRepository.save(createdProjectStack);
            }
        });

        //이미지 넣기
        multipartFiles.stream().forEach(multipartFile -> {
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

        return ProjectResponseDto.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .build();
    }

    public ProjectShortInfoResponseDto getShortInfos(){

        String email = SecurityUtil.getCurrentLoginUserId();

        List<Project> projects = projectRepository.findAllByUserEmail(email);

        List<ProjectResponseDto> responseDtos = projects.stream().map(project -> project.toShortInfo())
                .collect(Collectors.toList());

        return ProjectShortInfoResponseDto.builder()
                .projects(responseDtos)
                .build();
    }

    public Project getProject(Integer projectId) {

        String email = SecurityUtil.getCurrentLoginUserId();
        List<Project> projects = projectRepository.findAllByUserEmail(email);
        Project project = null;

        for(Project curProject : projects) {
            if(curProject.getId() == projectId) project = curProject;
        }

        return project;
    }

    public List<TroubleShootingsResponseDto> getTroubleShootings(Integer projectId) {
        List<TroubleShootingsResponseDto> troubleShootingsResponseDtos = new ArrayList<>();

        //본인이 가지고있는 프로젝트인지 인증까지
        Project project = getProject(projectId);

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
                List<String> tsPatchCodes = gitPatchCodeUtil.parsePatchCode(curGitFile.getPatchCode());
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
}
