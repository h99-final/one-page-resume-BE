package com.f5.onepageresumebe.web.git.controller;

import com.f5.onepageresumebe.domain.git.service.MGitService;
import com.f5.onepageresumebe.domain.task.service.TaskService;
import com.f5.onepageresumebe.web.common.dto.ResDto;
import com.f5.onepageresumebe.web.git.dto.FileDto;
import com.f5.onepageresumebe.web.git.dto.RepoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MGitController {
    private final MGitService mGitService;
    private final TaskService taskService;

    @Secured("ROLE_USER")
    @GetMapping("/project/{projectId}/git/sync")
    public ResDto sync(@PathVariable("projectId") Integer projectId){

        mGitService.order(projectId);

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

    @Secured("ROLE_USER")
    @GetMapping("/project/{projectId}/git/completion")
    public ResDto isCompletion(@PathVariable("projectId") Integer projectId) {
        HashMap<String, Object> res = new HashMap<>();

        Integer totalCommitCount = taskService.getTotalCommitCount(projectId);
        Integer curCommitCount = taskService.getCurCommitCount(projectId);
        Boolean isDone = taskService.isCompletion(projectId);

        res.put("totalCommitCount", totalCommitCount);
        res.put("curCommitCount", curCommitCount);
        res.put("isDone", isDone);


        return ResDto.builder()
                .result(true)
                .data(res)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/git/repo/validation")
    public ResDto gitRepoValidation(@RequestBody RepoDto.Request request) {

        HashMap<String, Boolean> res = new HashMap<>();

        res.put("isOk", mGitService.gitRepoValidation(request));

        return ResDto.builder()
                .result(true)
                .data(res)
                .build();
    }
}
