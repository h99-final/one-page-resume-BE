package com.f5.onepageresumebe.domain.entity;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends TimeEntity{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(unique = true,nullable = false, columnDefinition = "varchar(30)")
    private String email;

    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String password;

    @Column(columnDefinition = "varchar(10)")
    private String name;

    @Column(columnDefinition = "varchar(100)")
    private String githubUrl;

    @Column(columnDefinition = "varchar(100)")
    private String blogUrl;

    @Column(columnDefinition = "varchar(20)")
    private String phoneNum;

    @Column(columnDefinition = "varchar(20)")
    private String job;

    @Column(columnDefinition = "varchar(300)")
    private String profileImgUrl;

    @OneToMany(mappedBy = "user")
    private List<UserStack> userStackList = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "user")
    private List<Project> projectList = new ArrayList<>();

    @Column(name="user_role", columnDefinition = "varchar(10)")
    private String role = "ROLE_USER";

    @Builder(access = AccessLevel.PRIVATE)
    public User(String email, String password, String name, String githubUrl, String blogUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.role = "ROLE_USER";
        this.profileImgUrl = "https://myclone.s3.ap-northeast-2.amazonaws.com/profile/%EA%B2%80%EC%A0%95+%EC%82%AC%EC%A7%84.png";
    }

    public static User create(String email, String password, String name, String githubUrl, String blogUrl){

        return User.builder()
                .email(email)
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
        this.profileImgUrl = "https://myclone.s3.ap-northeast-2.amazonaws.com/profile/%EA%B2%80%EC%A0%95+%EC%82%AC%EC%A7%84.png";
    }

    public void setPortfolio(Portfolio portfolio){
        this.portfolio = portfolio;
    }
}
