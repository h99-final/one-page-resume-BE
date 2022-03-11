package com.f5.onepageresumebe.domain.entity;

import com.f5.onepageresumebe.exception.customException.CustomException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Career  {

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

    @Builder(access = AccessLevel.PRIVATE)
    public Career(String title, String subTitle, String contents, LocalDate startTime, LocalDate endTime, Portfolio portfolio) {
        this.title = title;
        this.subTitle = subTitle;
        this.contents = contents;
        this.startTime = startTime;
        this.endTime = endTime;
        this.portfolio = portfolio;
    }

    public static Career create(String title, String subTitle, String contents, LocalDate startTime, LocalDate endTime, Portfolio portfolio){

        Career career = Career.builder()
                .title(title)
                .subTitle(subTitle)
                .contents(contents)
                .startTime(startTime)
                .endTime(endTime)
                .portfolio(portfolio)
                .build();

        portfolio.getCareerList().add(career);

        return career;
    }


    public void updateCareer(String title, String subTitle, String contents, LocalDate startTime, LocalDate endTime){

        this.title=title;
        this.subTitle = subTitle;
        this.contents= contents;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private LocalDate convertEndTime(String endTimeString){

        LocalDate endTime = null;
        if("current".equals(endTimeString)){
            endTime = LocalDate.of(3000,1,1);
        }else{
            String[] split = endTimeString.split("-");
            Integer year = Integer.valueOf(split[0]);
            Integer month = Integer.valueOf(split[1]);
            Integer day = Integer.valueOf(split[2]);
            endTime = LocalDate.of(year,month,day);
        }

        return endTime;
    }

    private void validateDate(LocalDate startTime, LocalDate endTime){

        if(startTime.isAfter(endTime)){
            throw new CustomException("직무 경험 시작일은 직무 경험 종료일 보다 앞선 날짜여야 합니다.",INVALID_INPUT_ERROR);
        }
    }

}
