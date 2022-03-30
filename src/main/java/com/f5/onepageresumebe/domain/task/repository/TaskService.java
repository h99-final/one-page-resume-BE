package com.f5.onepageresumebe.domain.task.repository;

import com.f5.onepageresumebe.domain.git.entity.MCommit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    public Integer getCommitPercent(Integer projectId) {
        Integer totalCommitCount = taskRepository.getCommitCount(projectId);

        Query query = new Query(Criteria.where("projectId").is(projectId));
        Long curCommitCount = mongoTemplate.count(query, MCommit.class);

        Double percent = .0;

        if(curCommitCount != 0L) {
            percent = (((double)curCommitCount.intValue() / (double)totalCommitCount) * 100);
        }

        return percent.intValue();
    }

}
