package com.f5.onepageresumebe.domain.mysql.repository.querydsl;

import com.f5.onepageresumebe.domain.mysql.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.mysql.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.mysql.entity.QStack.stack;
import static com.f5.onepageresumebe.domain.mysql.entity.QUser.user;
import static com.f5.onepageresumebe.domain.mysql.entity.QUserStack.userStack;


@RequiredArgsConstructor
@Repository
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    //유저 id로 유저를 찾아 유저의 스택을 모두 반환
    public List<String> findStackNamesByUserId(Integer userId){

        List<String> stacks = queryFactory.select(stack.name).from(userStack)
                .innerJoin(userStack.stack,stack)
                .where(userStack.user.id.eq(userId))
                .fetch();

        return stacks;
    }

    //포트폴리오 id로 유저를 찾아 유저의 스택을 모두 반환
    public List<String> findStackNamesByPorfId(Integer porfId){

        List<String> stacks = queryFactory.selectDistinct(stack.name).from(userStack)
                .innerJoin(userStack.stack, stack)
                .innerJoin(userStack.user, user)
                .innerJoin(user.portfolio, portfolio)
                .where(portfolio.id.eq(porfId))
                .fetch();

        return stacks;
    }

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
