package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.PortfolioRespository;
import com.f5.onepageresumebe.domain.repository.UserRepository;
import com.f5.onepageresumebe.domain.repository.UserStackRepository;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.security.jwt.TokenProvider;
import com.f5.onepageresumebe.web.dto.jwt.TokenDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.AddInfoRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.CheckEmailRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.LoginRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.SignupRequestDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.AddInfoResponseDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.LoginResponseDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.LoginResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.f5.onepageresumebe.security.jwt.TokenProvider.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PortfolioRespository portfolioRespository;
    private final TokenProvider tokenProvider;
    private final StackService stackService;
    private final UserStackRepository userstackRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public Boolean registerUser(SignupRequestDto requestDto) {

        String email = requestDto.getEmail();
        //비밀번호, 비밀번호 확인 체크
        if(!requestDto.getPassword().equals(requestDto.getPasswordCheck())){
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 다릅니다");
        }

        // 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());
        User user = User.create(email, password, null, null, null);

        Boolean res;

        if(user == null) res = false;
        else {
            Portfolio portfolio = Portfolio.create(user);
            user.setPortfolio(portfolio);
            userRepository.save(user);
            portfolioRespository.save(portfolio);
            res = true;
        }

        return res;
    }

    public boolean checkEmail(CheckEmailRequestDto request) {
        boolean res = false;

        Optional<User> found = userRepository.findByEmail(request.getEmail());
        if(found.isPresent()) res = true;

        return res;
    }

    //로그인 서비스
    //존재하지 않거나 비밀번호가 맞지 않을시 오류를 내주고 그렇지 않을경우 토큰을 발행합니다.
    public LoginResultDto login(LoginRequestDto loginDto) {

        // login ID/Password를 기반으로 Authentication 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        // 실제로 검증 -> userdetailsService -> loaduserbyusername에서 검증
        // 비밀번호 검증은 DaoAuthenticationProvider에서 구현되어있음!
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        //JWT생성
        TokenDto tokenDto = tokenProvider.generateToken(authentication);

        //유저 정보 가져오기
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 email 입니다."));

        Boolean isFirstLogin;
        Integer portfolioId = user.getPortfolio().getId();
        Integer userId = user.getId();
        String email = user.getEmail();
        List<String> stack = new ArrayList<>();

        //첫 로그인 유저 O
        if(user.getCreatedAt().equals(user.getUpdatedAt()) == true) {
            isFirstLogin = true;
        }
        else { // 첫 로그인 유저 X (유저의 스텍 까지 넣어주기)
            isFirstLogin = false;
            //유저가 가지고 있는 UserStack 가져오기
            List<UserStack> userStackList = user.getUserStackList();
            //UserStack에 매칭되어있는 stack의 이름 가져오기
            for(UserStack u : userStackList) {
                stack.add(u.getStack().getName());
            }
        }
        LoginResponseDto loginResponseDto = new LoginResponseDto(isFirstLogin, portfolioId, userId, email, stack);

        return LoginResultDto.builder()
                .responseDto(loginResponseDto)
                .tokenDto(tokenDto)
                .build();
    }


    // 추가기입
    @Transactional
    public AddInfoResponseDto addInfo(AddInfoRequestDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 email 입니다."));

        List<String> stacks = requestDto.getStack();

        User curUser = userRepository.getById(user.getId());

        //사용자가 추가기입 시, 입력한 stack이 기존 stack테이블에 있으면 stack을 가져옴
        // 기존 stack 테이블이 없으면 새로 생성해 가져옴
        for(String curStackName : stacks) {
            Stack stack = stackService.registerStack(curStackName);
            UserStack userStack = UserStack.create(curUser, stack);

            userstackRepository.save(userStack);

            //유저 정보의 userstackList에 userstack 추가
            curUser.addStack(userStack);
        }

        // 추가 기입한 정보 유저에 넣기
        String name = requestDto.getName();
        String gitUrl = requestDto.getGitUrl();
        String blogUrl = requestDto.getBlogUrl();
        String phoneNum = requestDto.getPhoneNum();
        user.addInfo(name, gitUrl, blogUrl, phoneNum);
        userRepository.save(user);

        AddInfoResponseDto response = new AddInfoResponseDto(stacks);
        return response;
    }

    public void updateInfo(AddInfoRequestDto request) {
        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User curUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 email 입니다."));

        //Todo : stack관련
        curUser.updateInfo(request.getName(), request.getPhoneNum(), request.getGitUrl(), request.getBlogUrl());
        userRepository.save(curUser);
    }

    public HttpHeaders tokenToHeader(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, tokenDto.getAccessToken());

        return headers;
    }
}