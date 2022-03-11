package com.f5.onepageresumebe.security.jwt;

import com.f5.onepageresumebe.web.dto.jwt.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer";
    private static final String AUTHORITY_KEY = "auth";
    private static final String BEARER_TYPE = "bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 3 ;

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey){
        byte[] encode = Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8));
        this.key = Keys.hmacShaKeyFor(encode);
    }


    public TokenDto generateToken(Authentication authentication){
        //권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        //Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITY_KEY,authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();


        return TokenDto.builder()
                .accessToken(accessToken)
                .grantType(BEARER_TYPE)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken){
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITY_KEY) == null){
            throw new IllegalArgumentException("권한 정보가 없는 토큰입니다");
        }

        //권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITY_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        //UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }

    public Integer validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 1;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            //throw new CustomAuthenticationException("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            //throw new CustomAuthenticationException("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            //throw new CustomAuthenticationException("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            //throw new CustomAuthenticationException("JWT 토큰이 잘못되었습니다");
        }
        return 0;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}

