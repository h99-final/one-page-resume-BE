package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.service.GitService;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.gitCommit.requestDto.CommitRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
public class GitController {

    private final GitService gitService;

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
    public ResDto createTroubleShooting(@RequestBody CommitRequestDto request, @PathVariable("projectId") Integer projectId) {

        return ResDto.builder()
                .result(gitService.createTroubleShooting(projectId, request))
                .build();
    }
}
