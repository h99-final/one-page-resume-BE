package com.f5.onepageresumebe.domain.user.entity;

import com.f5.onepageresumebe.domain.portfolio.entity.PortfolioBookmark;
import com.f5.onepageresumebe.domain.project.entity.ProjectBookmark;
import com.f5.onepageresumebe.domain.common.TimeEntity;
import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.project.entity.Project;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends TimeEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(unique = true,nullable = false, columnDefinition = "varchar(100)")
    private String email;

    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String password;

    @Column(columnDefinition = "varchar(10)")
    private Boolean isKakao;

    @Column(columnDefinition = "varchar(100)")
    private String name;

    @Column(columnDefinition = "varchar(100)")
    private String githubUrl;

    @Column(columnDefinition = "varchar(100)")
    private String blogUrl;

    @Column(columnDefinition = "varchar(100)")
    private String phoneNum;

    @Column(columnDefinition = "varchar(100)")
    private String job;

    @Column(columnDefinition = "varchar(300)")
    private String profileImgUrl;

    @Column(columnDefinition = "varchar(300)")
    private String gitToken;

    @OneToMany(mappedBy = "user")
    private List<UserStack> userStackList = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Portfolio portfolio;

    @OneToMany(mappedBy = "user")
    private List<Project> projectList = new ArrayList<>();

    @Column(name="user_role", columnDefinition = "varchar(10)")
    private String role = "ROLE_USER";

    @OneToMany(mappedBy = "user")
    private List<ProjectBookmark> projectBookmarkList = new ArrayList<>();


    @OneToMany(mappedBy = "user")
    private List<PortfolioBookmark> portfolioBookmarkList = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    public User(String email, String password, String name, String githubUrl, String blogUrl,Boolean isKakao) {
        this.isKakao = isKakao;
        this.email = email;
        this.password = password;
        this.name = name;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.role = "ROLE_USER";
        this.profileImgUrl = "empty";
    }

    public static User create(String email, String password, String name, String githubUrl, String blogUrl){

        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .githubUrl(githubUrl)
                .blogUrl(blogUrl)
                .isKakao(false)
                .build();
    }

    public static User createKakao(String email, String password, String name, String githubUrl, String blogUrl){

        return User.builder()
                .email(email)
                .isKakao(true)
                .password(password)
                .name(name)
                .githubUrl(githubUrl)
                .blogUrl(blogUrl)
                .build();
    }

    public void addInfo(String name, String githubUrl, String blogUrl, String phoneNum,String job) {
        this.name = name;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.phoneNum = phoneNum;
        this.job = job;
    }

    public void updateInfo(String name, String phoneNum, String githubUrl, String blogUrl,String job) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.job = job;
    }

    public void updateProfile(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }

    public void deleteProfile(){
        this.profileImgUrl = "empty";
    }

    public void setPortfolio(Portfolio portfolio){
        this.portfolio = portfolio;
    }
    
    public void setGitToken(String gitToken){

        this.gitToken = gitToken;
    }


    public void changePassword(String password) {
        this.password = password;
    }
}
