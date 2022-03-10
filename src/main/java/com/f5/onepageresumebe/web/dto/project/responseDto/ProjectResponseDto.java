package com.f5.onepageresumebe.web.dto.project.responseDto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProjectResponseDto {

    private Integer id;
    private String title;
    private String imageUrl;
    private List<String> stack;
    private String content;
    private Integer bookmarkCount;
    private String userName;
    private String userJob;
}
