package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.Stack;
import com.f5.onepageresumebe.domain.repository.StackRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class StackService {
    private final StackRepository stackRepository;

    public Stack registerStack(String stackName) {

        //기존에 같은 이름의 스텍이 있는지
        Stack stack = stackRepository.findFirstByName(stackName).orElse(null);

        //기존에 같은 이름의 스텍이 없으면
        if(stack == null) {
            stack = Stack.create(stackName);
            stackRepository.save(stack);
        }
        return stack;
    }
}
