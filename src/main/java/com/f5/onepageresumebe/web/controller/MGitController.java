package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.mongoDB.service.MGitService;
import com.f5.onepageresumebe.domain.mysql.service.GitService;
import com.f5.onepageresumebe.web.dto.MGit.request.MGitRequestDto;
import com.f5.onepageresumebe.web.dto.MGit.response.MCommitMessageResponseDto;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MGitController {
    private final MGitService mGitService;

    @PostMapping("/git/sync")
    public ResDto sync(@RequestBody MGitRequestDto requestDto){

        mGitService.sync(requestDto);
        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @PostMapping("/git/commit")
    public ResDto getCommits(@RequestBody MGitRequestDto requestDto){

        return ResDto.builder()
                .result(true)
                .data(mGitService.getCommits(requestDto))
                .build();
    }

    @PostMapping("/git/commit/{sha1}/file")
    public ResDto getFiles(){

        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }
}
