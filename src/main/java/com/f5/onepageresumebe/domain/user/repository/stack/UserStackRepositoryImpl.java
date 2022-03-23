package com.f5.onepageresumebe.domain.user.repository.stack;

import com.f5.onepageresumebe.domain.user.repository.stack.UserStackRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.stack.entity.QStack.stack;
import static com.f5.onepageresumebe.domain.user.entity.QUser.user;
import static com.f5.onepageresumebe.domain.user.entity.QUserStack.userStack;

@RequiredArgsConstructor
public class UserStackRepositoryImpl implements UserStackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //유저 id로 유저를 찾아 유저의 스택을 모두 반환
    @Override
    public List<String> findStackNamesByUserId(Integer userId){

        List<String> stacks = queryFactory.select(stack.name).from(userStack)
                .innerJoin(userStack.stack,stack)
                .where(userStack.user.id.eq(userId))
                .fetch();

        return stacks;
    }

    //포트폴리오 id로 유저를 찾아 유저의 스택을 모두 반환
    @Override
    public List<String> findStackNamesByPorfId(Integer porfId){

        List<String> stacks = queryFactory.selectDistinct(stack.name).from(userStack)
                .innerJoin(userStack.stack, stack)
                .innerJoin(userStack.user, user)
                .innerJoin(user.portfolio, portfolio)
                .where(portfolio.id.eq(porfId))
                .fetch();

        return stacks;
    }
}
