package com.f5.onepageresumebe.domain.entity;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

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

    @Column(nullable = false, columnDefinition = "varchar(20)") //왜 안먹냐?
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role = UserRoleEnum.USER;

}
