package com.f5.onepageresumebe.web.project.controller;

import com.f5.onepageresumebe.domain.project.service.ProjectBookmarkService;
import com.f5.onepageresumebe.web.common.dto.ResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProjectBookmarkController {

    private final ProjectBookmarkService projectBookmarkService;

    @Secured("ROLE_USER")
    @PostMapping("bookmark/project/{projectId}")
    public ResDto addProjectBookmark(@PathVariable("projectId") Integer projectId) {

        projectBookmarkService.addProjectBookmark(projectId);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/bookmark/project/{projectId}")
    public ResDto deleteProjectBookmark(@PathVariable("projectId") Integer projectId) {

        projectBookmarkService.deleteProjectBookmark(projectId);

        return ResDto.builder()
                .result(true)
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/bookmark/project")
    public ResDto getProjectBookmark() {

        return ResDto.builder()
                .result(true)
                .data(projectBookmarkService.getProjectBookmark())
                .build();
    }


//    @Secured("ROLE_USER")
//    @PostMapping("/bookmark/porf/{porfId}") //북마크 추가
//    public ResDto addPortPolioBookmark(@PathVariable("porfId") Integer projectId) {
//
//        bookmarkService.addPortPolioBookmark(projectId);
//
//        return ResDto.builder()
//                .result(true)
//                .build();
//    }



//    @Secured("ROLE_USER")
//    @DeleteMapping("/bookmark/porf/{porfId}")
//    public ResDto deletePortPolioBookmark(@PathVariable("porfId") Integer projectId) {
//
//        bookmarkService.deletePortPolioBookmark(projectId);
//
//        return ResDto.builder()
//                .result(true)
//                .build();
//    }


//    @Secured("ROLE_USER")
//    @GetMapping("/bookmark/porf")
//    public ResDto getPortPolioBookmark() {
//
//        return ResDto.builder()
//                .result(true)
//                .data(bookmarkService.getPortPolioBookmark())
//                .build();
//    }
}