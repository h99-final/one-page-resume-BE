package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.entity.PortfolioStack;
import com.f5.onepageresumebe.domain.entity.Stack;
import com.f5.onepageresumebe.domain.repository.StackRepository;

public class StackUtil {

    public static Stack createStack(String stackName, StackRepository stackRepository) {
        Stack stack = stackRepository.findFirstByName(stackName).orElse(null);
        if (stack == null)
        {
            Stack stack1 = Stack.create(stackName);
            return stackRepository.save(stack1);
        }
        else return stack;
    }
}
