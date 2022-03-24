package com.f5.onepageresumebe.domain.career.repository;

import com.f5.onepageresumebe.domain.career.entity.Career;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.career.entity.QCareer.career;
import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.user.entity.QUser.user;


@RequiredArgsConstructor
public class CareerRepositoryImpl implements CareerRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Career> findByCareerIdAndUserEmail(Integer careerId,String userEmail){

        List<Career> careers = queryFactory.selectFrom(career)
                .innerJoin(career.portfolio, portfolio).fetchJoin()
                .innerJoin(portfolio.user, user).fetchJoin()
                .where(user.email.eq(userEmail).and(career.id.eq(careerId)))
                .limit(1)
                .fetch();

        if(careers.isEmpty()) return Optional.empty();
        else return Optional.of(careers.get(0));
    }
}
