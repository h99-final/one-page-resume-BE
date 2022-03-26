package com.f5.onepageresumebe.domain.apiCallCheck.repository;

import java.time.LocalDateTime;
import java.util.Map;

public interface ApiCallRepository {

    //생성
    void save(Integer userId);

    //전체 조회
    Map<Integer, LocalDateTime> findAllData();

    //존재 여부
    boolean existsById(Integer userId);

    //삭제
    void deleteById(Integer userId);

    //총 갯수
    Integer countAll();
}
