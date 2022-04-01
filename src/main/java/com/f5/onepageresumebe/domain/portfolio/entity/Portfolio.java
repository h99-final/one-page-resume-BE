package com.f5.onepageresumebe.domain.portfolio.entity;

import com.f5.onepageresumebe.domain.career.entity.Career;
import com.f5.onepageresumebe.domain.common.TimeEntity;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

public class Portfolio extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(100)")
    private String title;

    @Column(nullable = false,  columnDefinition = "SMALLINT")
    private Integer viewCount = 0;

    @Column(nullable = false ,columnDefinition = "TINYINT")
    private Integer templateIdx;

    @Column(columnDefinition = "varchar(500)")
    private String introContents;

    @Column(columnDefinition = "varchar(100)")
    private String githubUrl;

    @Column(columnDefinition = "varchar(100)")
    private String blogUrl;

    @Column(nullable = false,columnDefinition = "TINYINT")
    private Boolean isTemp;

    @Column(nullable = false,  columnDefinition = "SMALLINT")
    private Integer portBookmarkCount=0;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio")
    private List<Career> careerList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<Project> projectList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<PortfolioStack> portfolioStackList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<PortfolioBookmark> portfolioBookmarkList =new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    public Portfolio(String title, String introContents, String githubUrl, String blogUrl, User user) {

        this.title = title;
        this.viewCount = 0;
        this.templateIdx = 0;
        this.introContents = introContents;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.isTemp = true;
        this.user = user;
    }

    public static Portfolio create(User user){

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .build();

        user.setPortfolio(portfolio);
        return portfolio;
    }


    //소개글 업데이트에 대한 생성자 생성
    public void updateIntro(String title, String githubUrl, String introContents, String blogUrl){
        this.title= title;
        this.githubUrl= githubUrl;
        this.blogUrl =blogUrl;
        this.introContents= introContents;
    }


    //포트폴리오 템플릿 업데이트에 대한 생성자 생성
    public void updateTemplate(Integer templateIdx){

        this.templateIdx = templateIdx;
    }

    public boolean changeStatus(boolean show){

        this.isTemp = !show;

        return show;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public void reset(){
        this.title = null;
        this.viewCount = 0;
        this.templateIdx = 0;
        this.introContents = null;
        this.githubUrl = null;
        this.blogUrl = null;
        this.isTemp = true;
    }

    public void updatePortPolioBookmarkCount(Integer value) {
        this.portBookmarkCount += value;
    }


}