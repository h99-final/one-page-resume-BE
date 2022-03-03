//package com.f5.onepageresumebe.web.controller;
//
//import com.f5.onepageresumebe.domain.service.GitService;
//import com.f5.onepageresumebe.web.dto.common.ResDto;
//import com.f5.onepageresumebe.web.dto.git.requestDto.CreateRepoRequestDto;
//import com.f5.onepageresumebe.web.dto.git.responseDto.CreateRepoResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//public class GitController {
//
//    private final GitService gitService;
//
//    @PostMapping("/git/project/{projectId}/repository/{repoName}")
//    public ResDto createRepo(@RequestBody CreateRepoRequestDto requestDto,
//                             @PathVariable("projectId") Integer projectId,
//                             @PathVariable("repoName") String repoName){
//
//        Integer repoId = gitService.addRepository(projectId, repoName, requestDto.getRepoUrl());
//
//        return ResDto.builder()
//                .result(true)
//                .data(CreateRepoResponseDto.builder()
//                        .repoId(repoId)
//                        .build())
//                .build();
//    }
//
//    @GetMapping("/git/project/{projectId}/repository/{repoName}/commit")
//    public ResDto getCommits(@PathVariable("projectId") Integer projectId,
//                             @PathVariable("repoName") String repoName){
//
//        return ResDto.builder()
//                .result(true)
//                .data(null)
//                .build();
//    }
//
//}
