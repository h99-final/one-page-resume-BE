package com.f5.onepageresumebe.domain.task.repository;

public interface TaskRepository {

    void save(Integer projectId, Boolean isDone);

    boolean findByProjectId(Integer projectId);


}
