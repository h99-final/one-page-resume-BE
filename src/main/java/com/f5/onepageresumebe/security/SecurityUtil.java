package com.f5.onepageresumebe.security;

import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    public static String getCurrentLoginUserId(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || authentication.getName() == null){
            throw new CustomAuthenticationException("로그인이 필요합니다");
        }

        return authentication.getName();
    }
}
