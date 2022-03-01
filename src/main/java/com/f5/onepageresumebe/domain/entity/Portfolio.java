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
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column( nullable = false, columnDefinition = "varchar(100)")
    private String title;

    @Column(nullable = false,  columnDefinition = "SMALLINT")
    private Integer viewCount = 0;

    @Column(nullable = false ,columnDefinition = "TINYINT")
    private Integer templateIdx = 1; //todo: 기본 템플릿 확인

    @Column(nullable = false, columnDefinition = "varchar(500)")
    private String introContents;

    @Column(columnDefinition = "varchar(100)")
    private String url;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean isTemp;

    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String introBgImgUrl;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio")
    private List<Career> careerList = new ArrayList<>();
}
