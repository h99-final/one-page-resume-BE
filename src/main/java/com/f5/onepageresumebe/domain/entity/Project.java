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

    @OneToMany(mappedBy = "project")
    private List<ProjectStack> projectStackList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ProjectImg> projectImgList = new ArrayList<>();

    @OneToOne(mappedBy = "project")
    private GitRepository repository;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder(access = AccessLevel.PRIVATE)
    public Project(String title, String introduce, Portfolio portfolio,User user) {
        this.title = title;
        this.introduce = introduce;
        this.portfolio = portfolio;
        this.user = user;
    }

    public static Project create(String title, String introduce,User user) {

        Project project = Project.builder()
                .introduce(introduce)
                .title(title)
                .user(user)
                .build();

        user.getProjectList().add(project);

        return project;
    }

    void setRepository(GitRepository gitRepository){
        this.repository = gitRepository;
    }

    void setPortfolio(Portfolio portfolio){
        this.portfolio = portfolio;
        portfolio.getProjectList().add(this);
    }
}
