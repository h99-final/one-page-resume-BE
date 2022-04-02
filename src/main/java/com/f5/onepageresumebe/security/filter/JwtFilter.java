package com.f5.onepageresumebe.security.filter;

import com.f5.onepageresumebe.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.f5.onepageresumebe.security.jwt.TokenProvider.NOT_VALID;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    // 실제 필터링 로직
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext에 저장
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveAccessToken(request);

        // 정상적인 토큰이면 파싱 후 security context에 저장
        if (StringUtils.hasText(accessToken)) {
            Integer result = tokenProvider.validateToken(accessToken);
            if (result==1) {
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else if(result==NOT_VALID) {
                request.setAttribute("exception", "로그인 유지 시간이 만료되었습니다. 다시 로그인 해주세요");
            }else{
                request.setAttribute("exception","로그인 정보가 잘못되었습니다. 다시 로그인 해주세요");
            }
        }

        filterChain.doFilter(request, response);
    }


    //request에서 토큰 가져오기
    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }
}
