package com.f5.onepageresumebe.domain.mysql.repository.querydsl;

import com.f5.onepageresumebe.domain.mysql.entity.Career;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.mysql.entity.QCareer.career;
import static com.f5.onepageresumebe.domain.mysql.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.mysql.entity.QUser.user;

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
