package com.f5.onepageresumebe.domain.mysql.entity;

import com.f5.onepageresumebe.web.dto.project.ProjectDto;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProjectImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "varchar(1000)")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder(access = AccessLevel.PRIVATE)
    public ProjectImg(String imageUrl, Project project) {
        this.imageUrl = imageUrl;
        this.project = project;
    }

    public static ProjectImg create(Project project, String imageUrl){
        ProjectImg projectImg = ProjectImg.builder()
                .project(project)
                .imageUrl(imageUrl)
                .build();

        project.getProjectImgList().add(projectImg);

        return projectImg;
    }

    public ProjectDto.ImgResponse toProjectImgResponseDto(){

        return ProjectDto.ImgResponse.builder()
                .id(this.id)
                .url(this.imageUrl)
                .build();
    }
}
