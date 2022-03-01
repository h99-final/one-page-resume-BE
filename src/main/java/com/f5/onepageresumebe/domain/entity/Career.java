package com.f5.onepageresumebe.domain.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String title;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String subTitle;

    @Column(nullable = false, columnDefinition = "varchar(500)")
    private String contents; // 여러 줄 구분자로 구분하여 합친 것

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate startTime;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate endTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
}
