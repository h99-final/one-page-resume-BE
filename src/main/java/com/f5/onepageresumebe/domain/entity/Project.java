package com.f5.onepageresumebe.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Project extends TimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String title;

    @Column(nullable = false, columnDefinition = "varchar(5000)")
    private String introduce;

    @Column(columnDefinition = "varchar(30)")
    private String gitRepoName;

    @Column(columnDefinition = "varchar(100)")
    private String gitRepoUrl;

    @OneToMany(mappedBy = "project")
    private List<ProjectStack> projectStackList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ProjectImg> projectImgList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<GitCommit> gitCommitList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder(access = AccessLevel.PRIVATE)
    public Project(String title, String introduce,String gitRepoUrl,String gitRepoName ,Portfolio portfolio,User user) {
        this.title = title;
        this.introduce = introduce;
        this.portfolio = portfolio;
        this.user = user;
        this.gitRepoName = gitRepoName;
        this.gitRepoUrl = gitRepoUrl;
    }

    public static Project create(String title, String introduce,String gitRepoName,String gitRepoUrl,User user) {

        Project project = Project.builder()
                .introduce(introduce)
                .title(title)
                .gitRepoName(gitRepoName)
                .gitRepoUrl(gitRepoUrl)
                .user(user)
                .build();


        user.getProjectList().add(project);

        return project;
    }


    void setPortfolio(Portfolio portfolio){
        this.portfolio = portfolio;
        portfolio.getProjectList().add(this);
    }

}
