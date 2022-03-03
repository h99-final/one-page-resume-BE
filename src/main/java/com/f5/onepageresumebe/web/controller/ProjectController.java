package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.domain.service.ProjectService;
import com.f5.onepageresumebe.web.dto.project.requestDto.CreateProjectRequestDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.CreateProjectResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @Secured("ROLE_USER")
    @PostMapping("/project")
    public ResDto createProject(@RequestPart("images") List<MultipartFile> multipartFileList,
                                @RequestPart("data") CreateProjectRequestDto requestDto) {

        CreateProjectResponseDto responseDto = projectService.createProject(requestDto, multipartFileList);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }
}
