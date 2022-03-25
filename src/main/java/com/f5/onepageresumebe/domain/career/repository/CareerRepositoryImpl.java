package com.f5.onepageresumebe.domain.career.repository;

import com.f5.onepageresumebe.domain.career.entity.Career;
import com.f5.onepageresumebe.domain.career.entity.QCareer;
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
    public Boolean existsByCareerIdAndUserEmail(Integer careerId, String userEmail){

        Integer exists = queryFactory.selectOne()
                .from(QCareer.career)
                .innerJoin(QCareer.career.portfolio, portfolio).fetchJoin()
                .innerJoin(portfolio.user, user).fetchJoin()
                .where(user.email.eq(userEmail).and(QCareer.career.id.eq(careerId)))
                .fetchFirst();

        return exists != null;
    }

    @Override
    public Optional<Career> findByCareerIdAndUserEmail(Integer careerId,String userEmail){

        Career career = queryFactory.selectFrom(QCareer.career)
                .innerJoin(QCareer.career.portfolio, portfolio).fetchJoin()
                .innerJoin(portfolio.user, user).fetchJoin()
                .where(user.email.eq(userEmail).and(QCareer.career.id.eq(careerId)))
                .fetchFirst();

        return Optional.ofNullable(career);
    }
}
