package com.f5.onepageresumebe.domain.stack.entity;

import com.f5.onepageresumebe.domain.portfolio.entity.PortfolioStack;
import com.f5.onepageresumebe.domain.project.entity.ProjectStack;
import com.f5.onepageresumebe.domain.user.entity.UserStack;
import lombok.*;

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

    @Builder(access = AccessLevel.PRIVATE)
    public Stack(String name) {
        this.name = name;
    }

    public static Stack create(String name){
        return Stack.builder()
                .name(name)
                .build();
    }
}
