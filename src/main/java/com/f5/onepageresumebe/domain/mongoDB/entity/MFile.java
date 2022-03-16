package com.f5.onepageresumebe.domain.mongoDB.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class MFile {
    private String name;

    private String patchCode;

    @Builder
    public MFile(String name, String patchCode) {
        this.name = name;
        this.patchCode = patchCode;
    }

    public static MFile create(String name, String patchCode) {
        return MFile.builder()
                .name(name)
                .patchCode(patchCode)
                .build();
    }
}
