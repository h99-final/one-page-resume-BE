package com.f5.onepageresumebe.domain.task.service;

import com.f5.onepageresumebe.domain.git.entity.MCommit;
import com.f5.onepageresumebe.domain.task.repository.TaskRepository;
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
    private final MongoTemplate mongoTemplate;
    private final TaskRepository taskRepository;

    public Integer getCurCommitCount(Integer projectId) {

        Query query = new Query(Criteria.where("projectId").is(projectId));
        Long curCommitCount = mongoTemplate.count(query, MCommit.class);

        return curCommitCount.intValue();
    }
    public Integer getTotalCommitCount(Integer projectId) {

        Integer res = taskRepository.getCommitCount(projectId);
        if(res == null) res = 0;

        return res;
    }

}
