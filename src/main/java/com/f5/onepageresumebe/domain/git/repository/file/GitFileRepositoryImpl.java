package com.f5.onepageresumebe.domain.git.repository.file;

import com.f5.onepageresumebe.domain.git.entity.GitFile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.domain.git.entity.QGitCommit.gitCommit;
import static com.f5.onepageresumebe.domain.git.entity.QGitFile.gitFile;
import static com.f5.onepageresumebe.domain.project.entity.QProject.project;


@RequiredArgsConstructor
public class GitFileRepositoryImpl implements GitFileRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<GitFile> findFileByIdFetchAll(Integer fileId){

        List<GitFile> files = queryFactory.selectFrom(gitFile)
                .innerJoin(gitFile.commit, gitCommit).fetchJoin()
                .innerJoin(gitCommit.project, project).fetchJoin()
                .limit(1)
                .fetch();

        if(files.isEmpty()) return Optional.empty();
        else return Optional.of(files.get(0));
    }
    
}
