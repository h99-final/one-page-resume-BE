package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.service.GitService;
import com.f5.onepageresumebe.domain.service.ProjectService;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.gitCommit.requestDto.CommitRequestDto;
import com.f5.onepageresumebe.web.dto.gitCommit.responseDto.CommitIdResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class GitController {

    private final GitService gitService;
    private final ProjectService projectService;

    @Secured("ROLE_USER")
    @GetMapping("/git/project/{projectId}/commit")
    public ResDto getCommitMessages(@PathVariable("projectId") Integer projectId) {

        return ResDto.builder()
                .result(true)
                .data(gitService.getCommitMessages(projectId))
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/git/project/{projectId}/commit/{sha}/file")
    public ResDto getFiles(@PathVariable("projectId") Integer projectId, @PathVariable("sha") String sha) {

        return ResDto.builder()
                .result(true)
                .data(gitService.getFiles(projectId, sha))
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/project/{projectId}/troubleShooting")
    public ResDto createTroubleShooting(@Valid @RequestBody CommitRequestDto request, @PathVariable("projectId") Integer projectId) {
 
        CommitIdResponseDto commitIdResponseDto = gitService.createTroubleShooting(projectId, request);

        return ResDto.builder()
                .result(true)
                .data(commitIdResponseDto)
                .build();
    }


    @Secured("ROLE_USER")
    @PutMapping("/project/{projectId}/troubleShooting/{commitId}")
    public ResDto updateProjectTroubleShootings(@PathVariable("projectId") Integer projectId,
                                                @PathVariable("commitId") Integer commitId,
                                                @Valid @RequestBody CommitRequestDto request) {

        gitService.updateProjectTroubleShootings(projectId, commitId, request);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/project/{projectId}/troubleShooting/{commitId}")
    public ResDto deleteTroubleShooting(@PathVariable("projectId") Integer projectId, @PathVariable("commitId") Integer commitId) {

        projectService.deleteProjectTroubleShootings(projectId, commitId);

        return ResDto.builder()
                .result(true)
                .build();
    }
}
