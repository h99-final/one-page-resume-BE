package com.f5.onepageresumebe.web.git.controller;

import com.f5.onepageresumebe.domain.git.service.MGitService;
import com.f5.onepageresumebe.web.common.dto.ResDto;
import com.f5.onepageresumebe.web.git.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MGitController {
    private final MGitService mGitService;

    @Secured("ROLE_USER")
    @GetMapping("/project/{projectId}/git/sync")
    public ResDto sync(@PathVariable("projectId") Integer projectId){

        Long expectEndTime = mGitService.order(projectId);

        HashMap<String, Long> response = new HashMap<>();
        response.put("expectEndTime", expectEndTime);

        return ResDto.builder()
                .result(true)
                .data(response)
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
