package com.f5.onepageresumebe.domain.repository.querydsl;

import com.f5.onepageresumebe.domain.entity.Career;
import com.f5.onepageresumebe.domain.entity.QCareer;
import com.f5.onepageresumebe.domain.entity.QPortfolio;
import com.f5.onepageresumebe.domain.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.entity.QCareer.career;
import static com.f5.onepageresumebe.domain.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class CareerQueryRepository {

    private final JPAQueryFactory queryFactory;

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