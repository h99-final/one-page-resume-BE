package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.mongoDB.service.MGitService;
import com.f5.onepageresumebe.web.dto.MGit.request.MGitRequestDto;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import com.f5.onepageresumebe.web.dto.gitFile.responseDto.FilesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MGitController {
    private final MGitService mGitService;

    @Secured("ROLE_USER")
    @PostMapping("/git/sync")
    public ResDto sync(@RequestBody MGitRequestDto requestDto){

        mGitService.sync(requestDto);
        return ResDto.builder()
                .result(true)
                .data(null)
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/git/commit")
    public ResDto getCommits(@RequestBody MGitRequestDto requestDto){

        return ResDto.builder()
                .result(true)
                .data(mGitService.getCommits(requestDto))
                .build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/git/commit/{sha1}/file")
    public ResDto getFiles(@PathVariable("sha1") String sha1){

        List<FilesResponseDto> responseDtos = mGitService.findFilesBySha(sha1);

        return ResDto.builder()
                .result(true)
                .data(responseDtos)
                .build();
    }
}
