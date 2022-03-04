package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.service.GitService;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.gitCommit.requestDto.CommitRequestDto;
import com.f5.onepageresumebe.web.dto.gitFile.responseDto.FilesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GitController {

    private final GitService gitService;

    @GetMapping("/git/project/{projectId}/commit")
    public ResDto getCommitMessages(@PathVariable("projectId") Integer projectId) {

        return ResDto.builder()
                .result(true)
                .data(gitService.getCommitMessages(projectId))
                .build();
    }

    @GetMapping("/git/project/{projectId}/commit/{sha}/file")
    public ResDto getFiles(@PathVariable("projectId") Integer projectId, @PathVariable("sha") String sha) {

        return ResDto.builder()
                .result(true)
                .data(gitService.getFiles(projectId, sha))
                .build();
    }

    @PostMapping("/project/{projectId}/troubleShooting")
    public ResDto createTroubleShooting(@RequestBody CommitRequestDto request, @PathVariable("projectId") Integer projectId) {

        gitService.createTroubleShooting(projectId, request);

        return ResDto.builder()
                .result(true)
                .build();
    }
}
