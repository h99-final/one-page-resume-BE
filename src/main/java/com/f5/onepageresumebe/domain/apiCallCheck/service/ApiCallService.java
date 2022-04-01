package com.f5.onepageresumebe.domain.apiCallCheck.service;

import com.f5.onepageresumebe.domain.apiCallCheck.repository.ApiCallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCallService {

    private final ApiCallRepository apiCallRepository;

    public boolean callAvailability(Integer userId){

        //호출한지 20초가 덜 지났거나 총 호출 횟수가 20초안에 100번이 넘는다면 제한
        return !(apiCallRepository.existsById(userId) && apiCallRepository.countAll() <= 100);
    }

    public void call(Integer userId){

        apiCallRepository.save(userId);
    }

    @Scheduled(fixedRate = 1000*20)
    public void deleteByCallTime(){

        log.info("삭제 실행");

        Map<Integer, LocalDateTime> allData = apiCallRepository.findAllData();

        Set<Integer> userIds = allData.keySet();

        userIds.forEach(userId->{
            LocalDateTime createdAt = allData.get(userId);
            //초단위
            Duration duration = Duration.between(createdAt,LocalDateTime.now());
            //20초가 지났다면 다시 요청 가능하도록 삭제
            if(duration.getSeconds()>20){
                apiCallRepository.deleteById(userId);
            }
        });


    }
}
