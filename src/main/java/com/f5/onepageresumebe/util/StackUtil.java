package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.entity.PortfolioStack;
import com.f5.onepageresumebe.domain.entity.Stack;
import com.f5.onepageresumebe.domain.repository.StackRepository;

public class StackUtil {

    public static Stack createStack(String Stackname, StackRepository stackRepository) {
        Stack stack = stackRepository.findFirstByName(Stackname).orElse(null);
        if (stack == null)
        {

            return stackRepository.save(stack);
        }
        else return stack;
    }
}
