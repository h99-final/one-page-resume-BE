package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectSaveResponseDto;
import com.f5.onepageresumebe.web.dto.project.requestDto.ProjectRequestDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import com.f5.onepageresumebe.domain.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/porf/{porfId}/project") //포트폴리오 소개 작성
    public ProjectResponseDto createIntro(@RequestPart(value = "images") List<MultipartFile> multipartFiles
            , @RequestPart(value = "data") ProjectRequestDto projectRequestDto,
                                          @PathVariable("porfId") Integer porfId) throws IOException {

        ProjectSaveResponseDto response = projectService.createProject(projectRequestDto, multipartFiles);

        return ProjectResponseDto.builder()
                .result(true)
                .data(response)
                .build();
    }
}
