package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.mongoDB.service.MGitService;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.gitFile.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MGitController {
    private final MGitService mGitService;

    @Secured("ROLE_USER")
    @GetMapping("/project/{projectId}/git/sync")
    public ResDto sync(@PathVariable("projectId") Integer projectId){

        mGitService.sync(projectId);
        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/project/{projectId}/git/commit")
    public ResDto getCommits(@PathVariable("projectId") Integer projectId){

        return ResDto.builder()
                .result(true)
                .data(mGitService.getCommits(projectId))
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/project/{projectId}/git/commit/{sha1}/file")
    public ResDto getFiles(@PathVariable("sha1") String sha1){

        List<FileDto.Response> responseDtos = mGitService.findFilesBySha(sha1);

        return ResDto.builder()
                .result(true)
                .data(responseDtos)
                .build();
    }

}
