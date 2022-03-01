package com.f5.onepageresumebe.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProjectStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "stack_id")
    private Stack stack;

    @Builder(access = AccessLevel.PRIVATE)
    public ProjectStack(Project project, Stack stack) {
        this.project = project;
        this.stack = stack;
    }

    public static ProjectStack create(Project project, Stack stack){
        ProjectStack projectStack = ProjectStack.builder()
                .project(project)
                .stack(stack)
                .build();

        project.getProjectStackList().add(projectStack);
        stack.getProjectStackList().add(projectStack);

        return projectStack;
    }
}
