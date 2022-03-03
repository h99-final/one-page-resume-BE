package com.f5.onepageresumebe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class ProjectRequestDto {

    private String projectTitle;
    private String projectContent;
    private List<String> projectStack;
}
