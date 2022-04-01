package com.f5.onepageresumebe.domain.user.repository;

import com.f5.onepageresumebe.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findByEmail(String email){
        List<User> users = queryFactory.selectFrom(user)
                .innerJoin(user.portfolio, portfolio).fetchJoin()
                .where(user.email.eq(email))
                .limit(1)
                .fetch();

        if(users.isEmpty()) return Optional.empty();
        else return Optional.of(users.get(0));
    }
}
