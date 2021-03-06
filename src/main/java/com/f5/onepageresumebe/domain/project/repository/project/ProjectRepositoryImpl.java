package com.f5.onepageresumebe.domain.project.repository.project;

import com.f5.onepageresumebe.domain.project.entity.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.portfolio.entity.QPortfolio.portfolio;
import static com.f5.onepageresumebe.domain.project.entity.QProject.project;
import static com.f5.onepageresumebe.domain.project.entity.QProjectStack.projectStack;
import static com.f5.onepageresumebe.domain.stack.entity.QStack.stack;
import static com.f5.onepageresumebe.domain.user.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Project> findAllByUserEmail(String userEmail){

        List<Project> projects = queryFactory.selectFrom(project)
                .innerJoin(project.user, user).fetchJoin()
                .innerJoin(user.portfolio, portfolio).fetchJoin()
                .where(user.email.eq(userEmail))
                .fetch();

        return projects;
    }

    @Override
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

    @Override
    public List<Project> findAllByStackNames(List<String> stackNames,Pageable pageable){

        List<Project> projects = queryFactory.selectDistinct(project).from(projectStack)
                .innerJoin(projectStack.project, project)
                .innerJoin(projectStack.stack, stack)
                .innerJoin(project.user, user).fetchJoin()
                .where(stack.name.in(stackNames))
                .orderBy(project.bookmarkCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return projects;
    }

}
