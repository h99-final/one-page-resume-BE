package com.f5.onepageresumebe.domain.mysql.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GitCommit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commit_id")
    private Integer id;

    @Column(name = "commit_message", nullable = false, columnDefinition = "varchar(50)")
    private String message;

    @Column(name = "commit_sha", nullable = false, columnDefinition = "varchar(50)")
    private String sha;

    @Column(name = "commit_ts_name", nullable = false, columnDefinition = "varchar(50)")
    private String tsName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "commit")
    private List<GitFile> fileList = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)

    public GitCommit(String message, String sha,String tsName, Project project) {
        this.message = message;
        this.sha = sha;
        this.tsName = tsName;
        this.project = project;
    }

    public static GitCommit create(String message, String sha,String tsName, Project project){

        GitCommit commit = GitCommit.builder()
                .message(message)
                .sha(sha)
                .tsName(tsName)
                .project(project)
                .build();

        project.getGitCommitList().add(commit);

        return commit;
    }
}
