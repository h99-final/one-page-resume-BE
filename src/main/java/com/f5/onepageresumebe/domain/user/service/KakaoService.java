package com.f5.onepageresumebe.domain.user.service;

import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepository;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepositoryCustom;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.domain.user.repository.UserRepository;
import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.jwt.TokenProvider;
import com.f5.onepageresumebe.web.jwt.dto.TokenDto;
import com.f5.onepageresumebe.web.user.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.f5.onepageresumebe.security.jwt.TokenProvider.AUTHORIZATION_HEADER;
import static com.f5.onepageresumebe.security.jwt.TokenProvider.BEARER_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final PortfolioRepository portfolioRepository;

    @Value("${kakao.clientId}")
    private String clientId;

    @Value("${kakao.redirectUri}")
    private String redirectUri;

    private static final String GRANT_TYPE = "authorization_code";

    private static final String KAKAO_PREFIX = "kakao";

    @Transactional
    public UserDto.LoginResult forceLogin(String code) {

        //email 얻어옴
        String userEmail = getUserInfo(code);

        //카카오 유저는 kakao를 prefix로 붙임
        String kakaoUserEmail = KAKAO_PREFIX+userEmail;

        //email이 중복된 값인지 확인
        User user = userRepository.findByEmail(kakaoUserEmail).orElse(null);

        //첫 회원
        if(user==null){

            //회원 가입 절차 진행
            String password = passwordEncoder.encode(String.valueOf(UUID.randomUUID()));
            user = User.createKakao(kakaoUserEmail, password, null, null, null);
            userRepository.save(user);

            Portfolio portfolio = Portfolio.create(user);
            user.setPortfolio(portfolio);
            userRepository.save(user);
            portfolioRepository.save(portfolio);
        }

        //토큰, 인증 객체 생성 후 강제로 SecurityContext에 넣음
        //원래는 인증을 해야하지만 이미 카카오에서 인증을 했으므로 인증절차 X
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole());

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(simpleGrantedAuthority);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail()
                , user.getPassword(),authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //첫 로그인 유저가 아닐때
        boolean isFirstLogin = user.getCreatedAt().equals(user.getUpdatedAt());

        //클라이언트에서 받을 토큰 생성
        TokenDto tokenDto = tokenProvider.generateToken(authenticationToken);

        return UserDto.LoginResult.builder()
                .tokenDto(tokenDto)
                .loginResponseDto(UserDto.LoginResponse.builder()
                        .isFirstLogin(isFirstLogin)
                        .build())
                .build();
    }

    public String getUserInfo(String code){

        // authorization code -> token
        String accessToken = getAccessToken(code);

        // token -> userinfo
        String email = getUserEmailFromToken(accessToken);

        return email;
    }

    public String getAccessToken(String code) {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        log.info("code={}",code);

        //요청 보내고 응답 받기
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> res = rt.exchange("https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                req,
                String.class);

        //access토큰을 가져오자!
        String responseBody = res.getBody();

        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(responseBody);
            String accessToken = jsonNode.get("access_token").asText();
            log.info("accessToken={}",accessToken);
            return accessToken;
        } catch (JsonProcessingException e) {
            log.error("access token parsing error");
            throw new CustomException("access token을 받아오던중 문제가 발생하였습니다. 다시 시도해 주세요.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String getUserEmailFromToken(String accessToken) {

        //header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BEARER_PREFIX + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 보내고 결과값 받기
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                req,
                String.class
        );

        //요청 온거 파싱
        String responseBody = response.getBody();
        try {
            ObjectMapper om= new ObjectMapper();
            JsonNode jsonNode = om.readTree(responseBody);
            String email = jsonNode.get("kakao_account")
                    .get("email").asText();
            return email;
        } catch (JsonProcessingException e) {
            log.error("kakao user info parsing error");
            throw new CustomException("kakao user info를 받아오던중 문제가 발생하였습니다. 다시 시도해 주세요.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public HttpHeaders tokenToHeader(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, tokenDto.getAccessToken());

        return headers;
    }

}
