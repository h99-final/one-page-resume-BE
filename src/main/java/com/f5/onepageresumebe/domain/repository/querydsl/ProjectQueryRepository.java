package com.f5.onepageresumebe.domain.repository.querydsl;

import com.f5.onepageresumebe.domain.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.entity.QProject.project;
import static com.f5.onepageresumebe.domain.entity.QProjectStack.projectStack;
import static com.f5.onepageresumebe.domain.entity.QStack.stack;
import static com.f5.onepageresumebe.domain.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class ProjectQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Project> findAllByUserEmail(String userEmail){

        List<Project> projects = queryFactory.selectFrom(project)
                .innerJoin(project.user, user).fetchJoin()
                .innerJoin(user.portfolio, portfolio).fetchJoin()
                .where(user.email.eq(userEmail))
                .fetch();

        return projects;
    }

    public Optional<Project> findByUserEmailAndProjectId(String userEmail,Integer projectId){

        List<Project> projects = queryFactory.selectFrom(project)
                .innerJoin(project.user, user).fetchJoin()
                .innerJoin(user.portfolio, portfolio).fetchJoin()
                .where(project.id.eq(projectId).and(user.email.eq(userEmail)))
                .limit(1)
                .fetch();

        if(projects.isEmpty()) return Optional.empty();
        else return Optional.of(projects.get(0));
    }

    public List<Project> findAllByStackNames(List<String> stackNames){

        List<Project> projects = queryFactory.selectDistinct(project).from(projectStack)
                .innerJoin(projectStack.project, project)
                .innerJoin(projectStack.stack, stack)
                .innerJoin(project.user, user).fetchJoin()
                .where(stack.name.in(stackNames))
                .fetch();

        return projects;
    }


}
