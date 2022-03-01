package com.f5.onepageresumebe.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GitRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repository_id")
    private Integer id;

    @Column(name = "repository_name", nullable = false, columnDefinition = "varchar(30)")
    private String name;

    @Column(name = "repository_url",nullable = false, columnDefinition = "varchar(100)")
    private String url;

    @Column(name = "repository_readme", nullable =false, columnDefinition = "varchar(10000)")
    private String readme;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "repository")
    private List<GitCommit> gitCommitList = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    public GitRepository(String name, String url, String readme, Project project) {
        this.name = name;
        this.url = url;
        this.readme = readme;
        this.project = project;
    }

    public static GitRepository create(String name, String url, String readme, Project project){
        GitRepository gitRepository = GitRepository.builder()
                .name(name)
                .readme(readme)
                .project(project)
                .url(url)
                .build();

        project.setRepository(gitRepository);

        return gitRepository;
    }
}
