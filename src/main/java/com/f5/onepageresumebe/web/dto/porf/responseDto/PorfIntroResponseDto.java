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

    private String title;
    private String contents;
    private String githubUrl;
    private String blogUrl;
    private Integer viewCount;
    private String bgImage;
    private String modifiedAt;
    private Integer templateIdx;
}
