package com.f5.onepageresumebe.domain.service;


import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.domain.entity.ProjectImg;
import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.domain.repository.PortfolioRepository;
import com.f5.onepageresumebe.domain.repository.ProjectRepository;
import com.f5.onepageresumebe.domain.repository.UserRepository;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectSaveResponseDto;
import com.f5.onepageresumebe.web.dto.project.requestDto.ProjectRequestDto;
import com.f5.onepageresumebe.domain.repository.ProjectImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final S3Uploader s3Uploader;
    private final ProjectImgRepository projectImgRepository;
    private final UserRepository userRepository;

    @Transactional//프로젝트 생성
    public ProjectSaveResponseDto createProject(ProjectRequestDto projectRequestDto, List<MultipartFile> multipartFiles) throws IOException {

        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(email).get();

        String imageUrl = s3Uploader.uploads(multipartFiles,"ProjectImage");//사진 업로드

        Project project = Project.create(projectRequestDto.getProjectTitle(), projectRequestDto.getProjectContent(), user);
        project = projectRepository.save(project);

        ProjectImg projectImg = ProjectImg.create(project,imageUrl);
        
        projectImgRepository.save(projectImg);


        return ProjectSaveResponseDto.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .build();
    }
}
