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

    @OneToMany(mappedBy = "user")
    private List<UserStack> userStackList = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "user")
    private List<Project> projectList = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "varchar(20)") //왜 안먹냐?
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Builder(access = AccessLevel.PRIVATE)
    public User(String email, String password, String name, String githubUrl, String blogUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.role  = UserRoleEnum.USER;
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

    void setPortfolio(Portfolio portfolio){
        this.portfolio = portfolio;
    }
}
