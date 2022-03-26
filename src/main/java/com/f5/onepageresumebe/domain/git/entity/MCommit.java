package com.f5.onepageresumebe.domain.git.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MCommit {
    @Id
    private ObjectId id;

    @Field(targetType = FieldType.DATE_TIME, write = Field.Write.NON_NULL)
    @Indexed(unique = true)
    private Date date;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    @Indexed(unique = true)
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
    public MCommit(Date date, String sha, String message, String repoName, String repoOwner, List<MFile> files) {
        this.date = date;
        this.sha = sha;
        this.message = message;
        this.repoName = repoName;
        this.repoOwner = repoOwner;
        this.files = files;
    }

    public static MCommit create(Date date, String message, String sha, String repoName, String repoOwner, List<MFile> files){
        return MCommit.builder()
                .date(date)
                .message(message)
                .sha(sha)
                .repoName(repoName)
                .repoOwner(repoOwner)
                .files(files)
                .build();
    }
}
