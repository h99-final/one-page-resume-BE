package com.f5.onepageresumebe.domain.portfolio.entity;


import com.f5.onepageresumebe.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;


@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PortfolioBookmark {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;


    @Builder(access = AccessLevel.PRIVATE)
    public PortfolioBookmark(User user, Portfolio portfolio) {
        this.user = user;
        this.portfolio = portfolio;
    }

    public static PortfolioBookmark create(User user, Portfolio portfolio) {
        PortfolioBookmark portfolioBookmark = PortfolioBookmark.builder()
                .user(user)
                .portfolio(portfolio)
                .build();

        user.getPortfolioBookmarkList().add(portfolioBookmark);
        portfolio.getPortfolioBookmarkList().add(portfolioBookmark);

        return portfolioBookmark;
    }

//    public PorfDto.BookmarkResponse toPorfBookmarkReadResDTO() {
//        return new PorfResponseDto(this.id,this.getUser().getName(),this.toPorfBookmarkReadResDTO().getUserStack(),this.getPortfolio().getTitle(),this.getPortfolio().getTemplateIdx(),this.getUser().getJob());
//    }
}