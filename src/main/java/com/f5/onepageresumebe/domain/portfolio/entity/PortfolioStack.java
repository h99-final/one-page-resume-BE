package com.f5.onepageresumebe.domain.portfolio.entity;

import com.f5.onepageresumebe.domain.stack.entity.Stack;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PortfolioStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "stack_id")
    private Stack stack;

    @Builder(access = AccessLevel.PRIVATE)
    public PortfolioStack(Portfolio portfolio, Stack stack) {
        this.portfolio = portfolio;
        this.stack = stack;
    }

    public static PortfolioStack create(Portfolio portfolio, Stack stack){

        PortfolioStack portfolioStack = PortfolioStack.builder()
                .portfolio(portfolio)
                .stack(stack)
                .build();

        stack.getPortfolioStackList().add(portfolioStack);
        portfolio.getPortfolioStackList().add(portfolioStack);

        return portfolioStack;
    }
}
