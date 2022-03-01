package com.f5.onepageresumebe.domain.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, columnDefinition = "varchar(30)")
    private String name;

    @OneToMany(mappedBy = "stack")
    private List<UserStack> userStackList = new ArrayList<>();

    @OneToMany(mappedBy = "stack")
    private List<ProjectStack> projectStackList = new ArrayList<>();

    @OneToMany(mappedBy = "stack")
    private List<PortfolioStack> portfolioStackList = new ArrayList<>();

}
