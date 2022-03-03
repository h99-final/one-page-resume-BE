package com.f5.onepageresumebe.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {

    private String projectTitle;
    private String projectContent;
    private List<String> projectStack;
}
