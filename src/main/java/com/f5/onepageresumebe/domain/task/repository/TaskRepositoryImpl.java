package com.f5.onepageresumebe.domain.task.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class TaskRepositoryImpl implements TaskRepository {

    Map<Integer, Boolean> data = new ConcurrentHashMap<>();

    @Override
    public void save(Integer projectId, Boolean isDone) {

        log.info("projectId:{}",projectId);
        log.info("inDone:{}",isDone);

        boolean isExists = data.containsKey(projectId);

        if (isExists) {
            //이미 존재하는 projectId면 값 업데이트
            data.replace(projectId, isDone);
        } else {
            //존재하지 않는다면 생성
            data.put(projectId, isDone);
        }
    }

    @Override
    public boolean findByProjectId(Integer projectId) {

        //작업이 끝났는지 확인
        Boolean isDone = data.get(projectId);

        //작업이 끝났다면 삭제
        if(isDone){
            data.remove(projectId);
        }

        return isDone;
    }
}
