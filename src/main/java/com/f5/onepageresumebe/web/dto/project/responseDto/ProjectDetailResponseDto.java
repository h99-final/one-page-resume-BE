package com.f5.onepageresumebe.web.dto.project.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectDetailResponseDto {

    private Integer id;
    private String title;
    private String content;
    private String imgUrl;
    private List<String> stack;
    private Integer bookmarkCount;
}
