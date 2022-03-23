package com.f5.onepageresumebe.domain.project.entity;

import com.f5.onepageresumebe.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProjectBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder(access = AccessLevel.PRIVATE)
    public ProjectBookmark(User user, Project project) {
        this.user = user;
        this.project = project;
    }

    public static ProjectBookmark create(User user, Project project) {
        ProjectBookmark projectBookmark = ProjectBookmark.builder()
                .user(user)
                .project(project)
                .build();

        user.getProjectBookmarkList().add(projectBookmark);
        project.getProjectBookmarkList().add(projectBookmark);

        return projectBookmark;
    }
}