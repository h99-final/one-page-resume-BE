package com.f5.onepageresumebe.domain.mongoDB.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.ArrayList;
import java.util.List;

@Document
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MCommit {
    @Id
    private ObjectId id;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    @Indexed
    private String sha;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String message;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String repoName;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String repoOwner;

    @Field(targetType = FieldType.ARRAY, write = Field.Write.NON_NULL)
    private List<MFile> files = new ArrayList();

    @Builder
    public MCommit(String sha, String message, String repoName, String repoOwner, List<MFile> files) {
        this.sha = sha;
        this.message = message;
        this.repoName = repoName;
        this.repoOwner = repoOwner;
        this.files = files;
    }

    public static MCommit create(String message, String sha, String repoName, String repoOwner, List<MFile> files){
        return MCommit.builder()
                .message(message)
                .sha(sha)
                .repoName(repoName)
                .repoOwner(repoOwner)
                .files(files)
                .build();
    }
}
