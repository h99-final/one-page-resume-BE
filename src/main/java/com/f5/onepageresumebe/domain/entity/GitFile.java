package com.f5.onepageresumebe.domain.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GitFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer id;

    @Column(name = "file_name", nullable = false, columnDefinition = "varchar(300)")
    private String name;

    @Column(name = "file_patch_code", nullable = false, columnDefinition = "varchar(10000)")
    private String patch_code;

    @Column(name = "file_trouble_contents", nullable = false, columnDefinition = "varchar(5000)")
    private String troubleContents;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "commit_id")
    private GitCommit commit;
}
