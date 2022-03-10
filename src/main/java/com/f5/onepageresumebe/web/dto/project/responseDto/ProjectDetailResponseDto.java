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
    private String imageUrl;
    private List<String> stack;
    private Integer bookmarkCount;
    private String content;
    private String userName;
    private String userJob;
    private Boolean isMyProject;
    private Boolean isBookmarking;

    public void checkBookmark(boolean isMyProject, boolean isBookmarking) {
        this.isMyProject = isMyProject;
        this.isBookmarking = isBookmarking;
    }
}
