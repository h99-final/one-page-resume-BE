package com.f5.onepageresumebe.web.controller;

import com.f5.onepageresumebe.domain.mysql.service.BookmarkService;
import com.f5.onepageresumebe.web.dto.common.ResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Secured("ROLE_USER")
    @PostMapping("bookmark/{projectId}")
    public ResDto addProjectBookmark(@PathVariable("projectId") Integer projectId) {

        bookmarkService.addProjectBookmark(projectId);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/bookmark/{projectId}")
    public ResDto deleteProjectBookmark(@PathVariable("projectId") Integer projectId) {

        bookmarkService.deleteProjectBookmark(projectId);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/bookmark/project")
    public ResDto getProjectBookmark() {

        return ResDto.builder()
                .result(true)
                .data(bookmarkService.getProjectBookmark())
                .build();
    }
}