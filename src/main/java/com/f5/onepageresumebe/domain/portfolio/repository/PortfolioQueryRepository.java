package com.f5.onepageresumebe.domain.portfolio.repository;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolioStack.portfolioStack;
import static com.f5.onepageresumebe.domain.project.entity.QProject.project;
import static com.f5.onepageresumebe.domain.project.entity.QProjectImg.projectImg;
import static com.f5.onepageresumebe.domain.stack.entity.QStack.stack;
import static com.f5.onepageresumebe.domain.user.entity.QUser.user;


@RequiredArgsConstructor
@Repository
public class PortfolioQueryRepository {

    private final JPAQueryFactory queryFactory;


    public Optional<Portfolio> findByUserEmailFetchUser(String email) {

        List<Portfolio> portfolios = queryFactory.selectFrom(portfolio)
                .innerJoin(portfolio.user, user).fetchJoin()
                .where(user.email.eq(email))
                .limit(1L)
                .fetch();

        if (portfolios.isEmpty()) return Optional.empty();
        else return Optional.of(portfolios.get(0));
    }

    public List<Portfolio> findAllByStackNamesIfPublicLimit(List<String> stacks) {

        List<Portfolio> portfolios = queryFactory.select(portfolio).from(portfolioStack)
                .innerJoin(portfolioStack.portfolio, portfolio)
                .innerJoin(portfolioStack.stack, stack)
                .innerJoin(portfolio.user, user)
                .where(stack.name.in(stacks).and(portfolio.isTemp.eq(false)))
                .orderBy(portfolio.viewCount.desc())
                .limit(12L)
                .fetch();

        return portfolios;
    }

    //공개된 포트폴리오만 가져온다
    public List<Portfolio> findAllFetchUserIfPublicLimit() {

        List<Portfolio> portfolios = queryFactory.selectFrom(portfolio)
                .innerJoin(portfolio.user, user).fetchJoin()
                .where(portfolio.isTemp.eq(false))
                .orderBy(portfolio.viewCount.desc())
                .limit(12L)
                .fetch();

        return portfolios;
    }

    public List<String> findStackNamesByPorfId(Integer porfId) {

        List<String> stackNames = queryFactory.select(stack.name).from(portfolioStack)
                .innerJoin(portfolioStack.stack, stack)
                .where(portfolioStack.portfolio.id.eq(porfId))
                .fetch();

        return stackNames;
    }

    public Optional<ProjectImg> findFirstProjectImgByProjectId(Integer projectId) {

        List<ProjectImg> projectImgs = queryFactory.selectFrom(projectImg)
                .innerJoin(projectImg.project, project).fetchJoin()
                .where(project.id.eq(projectId))
                .limit(1)
                .fetch();

        if (projectImgs.isEmpty()) return Optional.empty();
        else return Optional.of(projectImgs.get(0));
    }

//    public Optional<Portfolio> findFirstPorfByUserEmail(String userEmail){
//
//        List<Portfolio> portfolios = queryFactory.selectFrom(portfolio)
//                .innerJoin(portfolio.user, user).fetchJoin()
//                .where(portfolio.id.eq(porfId).and(user.email.eq(userEmail)))
//                .limit(1)
//                .fetch();
//
//        if(portfolios.isEmpty()) return Optional.empty();
//        else return Optional.of(portfolios.get(0));
//    }
}
