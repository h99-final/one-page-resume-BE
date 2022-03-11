package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.domain.service.ProjectService;
import com.f5.onepageresumebe.web.dto.project.requestDto.ProjectRequestDto;
import com.f5.onepageresumebe.web.dto.project.requestDto.ProjectUpdateRequestDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectDetailResponseDto;
import com.f5.onepageresumebe.web.dto.project.responseDto.ProjectResponseDto;
import com.f5.onepageresumebe.web.dto.stack.StackDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @Secured("ROLE_USER")
    @PostMapping("/project")
    public ResDto createProject(@RequestPart("images") List<MultipartFile> multipartFileList,
                                @Valid @RequestPart("data") ProjectRequestDto requestDto) {

        ProjectResponseDto responseDto = projectService.createProject(requestDto, multipartFileList);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/project/{projectId}")
    public ResDto updateProjectIntro(@PathVariable("projectId") Integer projectId,
                                     @Valid @RequestBody ProjectUpdateRequestDto requestDto){

        projectService.updateProjectInfo(projectId,requestDto);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PutMapping("/project/{projectId}/image")
    public ResDto updateImages(@RequestPart("images") List<MultipartFile> multipartFiles,
                               @PathVariable("projectId") Integer projectId){

        projectService.updateProjectImages(projectId, multipartFiles);

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/user/project")
    public ResDto getProjectsByUser(){

        List<ProjectResponseDto> responseDto = projectService.getShortInfos();

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }

    @GetMapping("/project/{projectId}/troubleShooting")
    public ResDto getTroubleShootings(@PathVariable("projectId") Integer projectId) {

        return ResDto.builder()
                .result(true)
                .data(projectService.getTroubleShootings(projectId))
                .build();
  }

    @PostMapping("/project/stack")
    public ResDto getProjectsByStack(@RequestBody StackDto requestDto,
                                     @PageableDefault(size = 12) Pageable pageable){

        Page<ProjectResponseDto> responseDtos = projectService.getAllByStacks(requestDto,pageable);

        return ResDto.builder()
                .result(true)
                .data(responseDtos)
                .build();
    }

    @DeleteMapping("/project/{projectId}")
    public ResDto deleteProject(@PathVariable("projectId") Integer projectId) {

        projectService.deleteProject(projectId);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @GetMapping("/project/{projectId}")
    public ResDto getProjectDetail(@PathVariable("projectId") Integer projectId) {

        ProjectDetailResponseDto responseDto = projectService.getProjectDetail(projectId);

        return ResDto.builder()
                .result(true)
                .data(responseDto)
                .build();
    }
}
