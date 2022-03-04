package com.f5.onepageresumebe.domain.service;


import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import com.f5.onepageresumebe.web.dto.project.requestDto.CreateProjectRequestDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectShortInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final PortfolioRepository portfolioRepository;
    private  final S3Uploader s3Uploader;
    private final ProjectImgRepository projectImgRepository;
    private final UserRepository userRepository;


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

}
