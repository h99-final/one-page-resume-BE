package com.f5.onepageresumebe.web.dto.porf.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PorfIntroResponseDto {

    private String introduceTitle;
    private String githubUrl;
    private String blogUrl;
    private String introContents;
    private Integer viewCount;
    private String bgImage;
    private String modifiedAt;
    private Integer templateIdx;
}
