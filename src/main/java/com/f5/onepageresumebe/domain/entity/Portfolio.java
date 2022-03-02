package com.f5.onepageresumebe.domain.entity;

import com.f5.onepageresumebe.dto.careerDto.PorfIntroRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(100)")
    private String title;

    @Column(nullable = false,  columnDefinition = "SMALLINT")
    private Integer viewCount = 0;

    @Column(nullable = false ,columnDefinition = "TINYINT")
    private Integer templateIdx; //todo: 기본 템플릿 확인

    @Column(columnDefinition = "varchar(500)")
    private String introContents;

    @Column(columnDefinition = "varchar(100)")
    private String githubUrl;

    @Column(columnDefinition = "varchar(100)")
    private String blogUrl;

    @Column(nullable = false,columnDefinition = "TINYINT")
    private Boolean isTemp;

    @Column(columnDefinition = "varchar(100)")
    private String introBgImgUrl;

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
    public Portfolio(String title, String introContents, String githubUrl, String introBgImgUrl, String blogUrl, User user) {
        this.title = title;
        this.viewCount = 0;
        this.templateIdx = 1;
        this.introContents = introContents;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.isTemp = true;
        this.introBgImgUrl = introBgImgUrl;
        this.user = user;
    }




    public static Portfolio create(User user){

        Portfolio portfolio = Portfolio.builder()
                .build();

        user.setPortfolio(portfolio);

        return portfolio;
    }


    //소개글 업데이트에 대한 생성자 생성
    public void updateInrtro(String title, String githubUrl, String introBgImgUrl, String introContents, String blogUrl, User user){

        this.title= title;
        this.githubUrl= githubUrl;
        this.blogUrl =blogUrl;
        this.introBgImgUrl= introBgImgUrl;
        this.introContents= introContents;
        this.user = user;
    }

    public void updateTemplate(Integer templateIdx){
        this.templateIdx = templateIdx;
    }



   /* public Portfolio(PorfIntroRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.githubUrl = requestDto.getGithubUrl();
        this.introBgImgUrl = requestDto.getIntroBgImgUrl();
        this.introContents = requestDto.getIntroContents();
    }*/
}
