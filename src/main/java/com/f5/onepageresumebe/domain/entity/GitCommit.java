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
public class GitCommit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commit_id")
    private Integer id;

    @Column(name = "commit_message", nullable = false, columnDefinition = "varchar(50)")
    private String message;

    @Column(name = "commit_sha", nullable = false, columnDefinition = "varchar(50)")
    private String sha;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "repository_id")
    private GitRepository repository;

    @OneToMany(mappedBy = "commit")
    private List<GitFile> fileList = new ArrayList<>();
}
