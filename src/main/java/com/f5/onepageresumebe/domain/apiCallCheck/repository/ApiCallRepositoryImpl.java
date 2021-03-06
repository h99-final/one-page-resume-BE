package com.f5.onepageresumebe.domain.apiCallCheck.repository;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ApiCallRepositoryImpl implements ApiCallRepository {

    Map<Integer, LocalDateTime> data = new ConcurrentHashMap<>();

    @Override
    public void save(Integer userId) {
        data.put(userId,LocalDateTime.now());
    }

    @Override
    public Map<Integer, LocalDateTime> findAllData() {

        return new HashMap<>(data);
    }

    @Override
    public boolean existsById(Integer userId) {

        return data.containsKey(userId);
    }

    @Override
    public void deleteById(Integer userId) {

        data.remove(userId);
    }

    @Override
    public Integer countAll() {
        return data.size();
    }
}
