package com.f5.onepageresumebe.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

public class Portfolio extends TimeEntity{

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

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio")
    private List<Career> careerList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<Project> projectList = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<PortfolioStack> portfolioStackList = new ArrayList<>();

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

    public String changeStatus(String status){
        if (status.equals("public")){
            this.isTemp = false;
        }else if(status.equals("private")){
            this.isTemp = true;
        }else{
            throw new IllegalArgumentException("상태값은 public, private만 넣을 수 있습니다");
        }

        return status;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }
    
}
