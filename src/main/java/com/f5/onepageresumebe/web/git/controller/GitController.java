package com.f5.onepageresumebe.web.git.controller;

import com.f5.onepageresumebe.domain.common.check.DeleteService;
import com.f5.onepageresumebe.domain.git.service.GitService;
import com.f5.onepageresumebe.web.common.dto.ResDto;
import com.f5.onepageresumebe.web.git.dto.CommitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class GitController {

    private final GitService gitService;
    private final DeleteService deleteService;

    @Secured("ROLE_USER")
    @PostMapping("/project/{projectId}/troubleShooting")
    public ResDto createTroubleShooting(@Valid @RequestBody CommitDto.Request request, @PathVariable("projectId") Integer projectId) {
 
        CommitDto.IdResponse commitIdResponseDto = gitService.createTroubleShooting(projectId, request);

        return ResDto.builder()
                .result(true)
                .data(commitIdResponseDto)
                .build();
    }


    @Secured("ROLE_USER")
    @PutMapping("/project/{projectId}/troubleShooting/{commitId}")
    public ResDto updateProjectTroubleShootings(@PathVariable("projectId") Integer projectId,
                                                @PathVariable("commitId") Integer commitId,
                                                @Valid @RequestBody CommitDto.Request request) {

        gitService.updateProjectTroubleShootings(projectId, commitId, request);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/project/{projectId}/troubleShooting/{commitId}")
    public ResDto deleteTroubleShooting(@PathVariable("projectId") Integer projectId, @PathVariable("commitId") Integer commitId) {

        deleteService.deleteProjectTroubleShootings(projectId, commitId);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/project/{projectId}/troubleShooting/{commitId}/file/{fileId}")
    public ResDto deleteFile(@PathVariable("projectId") Integer projectId,
                             @PathVariable("commitId") Integer commitId,
                             @PathVariable("fileId") Integer fileId){

        deleteService.deleteFile(projectId,commitId,fileId);

        return ResDto.builder()
                .result(true)
                .build();
    }
}
