package com.f5.onepageresumebe.domain.portfolio.repository.portfolio;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolioStack.portfolioStack;
import static com.f5.onepageresumebe.domain.stack.entity.QStack.stack;
import static com.f5.onepageresumebe.domain.user.entity.QUser.user;


@RequiredArgsConstructor
@Repository
public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Portfolio> findByUserEmailFetchUser(String email) {

        List<Portfolio> portfolios = queryFactory.selectFrom(portfolio)
                .innerJoin(portfolio.user, user).fetchJoin()
                .where(user.email.eq(email))
                .limit(1L)
                .fetch();

        if (portfolios.isEmpty()) return Optional.empty();
        else return Optional.of(portfolios.get(0));
    }

    @Override
    public List<Portfolio> findAllByStackNamesIfPublicPaging(List<String> stacks,Pageable pageable) {

        List<Portfolio> portfolios = queryFactory.select(portfolio).from(portfolioStack)
                .innerJoin(portfolioStack.portfolio, portfolio)
                .innerJoin(portfolioStack.stack, stack)
                .innerJoin(portfolio.user, user)
                .where(stack.name.in(stacks).and(portfolio.isTemp.eq(false)))
                .orderBy(portfolio.viewCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return portfolios;
    }

    //공개된 포트폴리오만 가져온다
    @Override
    public List<Portfolio> findAllFetchUserIfPublicPaging(Pageable pageable) {

        List<Portfolio> portfolios = queryFactory.selectFrom(portfolio)
                .innerJoin(portfolio.user, user).fetchJoin()
                .where(portfolio.isTemp.eq(false))
                .orderBy(portfolio.viewCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return portfolios;
    }

    @Override
    public List<String> findStackNamesByPorfId(Integer porfId) {

        List<String> stackNames = queryFactory.select(stack.name).from(portfolioStack)
                .innerJoin(portfolioStack.stack, stack)
                .where(portfolioStack.portfolio.id.eq(porfId))
                .fetch();

        return stackNames;
    }

    @Override
    public boolean existsByUserEmailAndPorfId(String userEmail, Integer porfId) {

        Integer exists = queryFactory.selectOne()
                .from(portfolio)
                .innerJoin(portfolio.user, user)
                .where(portfolio.id.eq(porfId).and(user.email.eq(userEmail)))
                .fetchFirst();

        return exists!=null;
    }

}
