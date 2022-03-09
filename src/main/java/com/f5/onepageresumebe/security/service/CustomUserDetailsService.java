package com.f5.onepageresumebe.security.service;

import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.domain.repository.UserRepository;
import com.f5.onepageresumebe.domain.repository.querydsl.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userQueryRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(()->new UsernameNotFoundException(email + ": 해당 이메일 정보가 존재하지 않습니다."));

    }

    private UserDetails createUserDetails(User user){
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
