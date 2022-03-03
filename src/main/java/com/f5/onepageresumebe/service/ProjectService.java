package com.f5.onepageresumebe.service;


import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.dto.ProjectSaveResponseDto;
import com.f5.onepageresumebe.dto.ProjectRequestDto;
import com.f5.onepageresumebe.repository.PortfolioRepository;
import com.f5.onepageresumebe.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@RequiredArgsConstructor //롬북을 통해서 간단하게 생성자 주입 방식의 어노테이션으로 fjnal이 붙거나 @notNull이 붙은 생성자들을 자동 생성해준다.
@Service
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private  final S3Uploader s3Uploader;


    @Transactional//프로젝트 생성
    public ProjectSaveResponseDto createProject(Integer porfId, ProjectRequestDto projectRequestDto, MultipartFile[] multipartFiles) throws IOException {


        //유저 아이디로 포폴 아이디 가져오기
        Portfolio portfolio = portfolioRepository.findById(porfId).orElseThrow(
                () -> new NullPointerException("없습니다")
        );

       // imageUrl = s3Uploader.uploads(multipartFiles,"introImage");//사진 업로드

        Project project = Project.create(projectRequestDto.getProjectTitle(), projectRequestDto.getProjectContent(), portfolio);
        project = projectRepository.save(project);

        return ProjectSaveResponseDto.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .build();
    }
}
