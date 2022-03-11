package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.config.S3Uploader;
import com.f5.onepageresumebe.domain.entity.*;
import com.f5.onepageresumebe.domain.repository.*;
import com.f5.onepageresumebe.domain.repository.querydsl.UserQueryRepository;
import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.exception.customException.CustomImageException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.security.jwt.TokenProvider;
import com.f5.onepageresumebe.util.StackUtil;
import com.f5.onepageresumebe.web.dto.jwt.TokenDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.AddInfoRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.CheckEmailRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.LoginRequestDto;
import com.f5.onepageresumebe.web.dto.user.requestDto.SignupRequestDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.LoginResponseDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.LoginResultDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.UserImageResponseDto;
import com.f5.onepageresumebe.web.dto.user.responseDto.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.f5.onepageresumebe.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;
import static com.f5.onepageresumebe.security.jwt.TokenProvider.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final TokenProvider tokenProvider;
    private final StackRepository stackRepository;
    private final UserStackRepository userstackRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final S3Uploader s3Uploader;
    private final UserQueryRepository userQueryRepository;

    @Transactional
    public Boolean registerUser(SignupRequestDto requestDto) {

        String email = requestDto.getEmail();
        //비밀번호, 비밀번호 확인 체크
        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new CustomException("비밀번호와 비밀번호 확인이 다릅니다", INVALID_INPUT_ERROR);
        }

        // 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());
        User user = User.create(email, password, null, null, null);

        boolean res = false;

        if (user != null) {
            Portfolio portfolio = Portfolio.create(user);
            user.setPortfolio(portfolio);
            userRepository.save(user);
            portfolioRepository.save(portfolio);
            res = true;
        }

        return res;
    }

    public boolean checkEmail(CheckEmailRequestDto request) {
        boolean res = false;

        Optional<User> found = userQueryRepository.findByEmail(request.getEmail());
        if (found.isPresent()) res = true;

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
        //아이디가 잘못된 값이면 여기까지 안오지만..
        User user = userQueryRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new CustomAuthenticationException("Email을 확인해 주세요"));

        //첫 로그인 유저가 아닐때
        boolean isFirstLogin = user.getCreatedAt().equals(user.getUpdatedAt());


        return LoginResultDto.builder()
                .tokenDto(tokenDto)
                .loginResponseDto(LoginResponseDto.builder()
                        .isFirstLogin(isFirstLogin)
                        .build())
                .build();
    }


    // 추가기입
    @Transactional
    public void addInfo(AddInfoRequestDto requestDto) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        List<String> stacks = requestDto.getStack();

        if(stacks.size()<3){
            throw new CustomException("기술 스택은 3개 이상 입력해 주세요",INVALID_INPUT_ERROR);
        }

        User curUser = userRepository.getById(user.getId());

        //사용자가 추가기입 시, 입력한 stack이 기존 stack테이블에 있으면 stack을 가져옴
        // 기존 stack 테이블이 없으면 새로 생성해 가져옴
        for (String curStackName : stacks) {
            Stack stack = StackUtil.createStack(curStackName, stackRepository);
            UserStack userStack = UserStack.create(curUser, stack);

            userstackRepository.save(userStack);

        }

        // 추가 기입한 정보 유저에 넣기
        String name = requestDto.getName();
        String gitUrl = requestDto.getGitUrl();
        String blogUrl = requestDto.getBlogUrl();
        String phoneNum = requestDto.getPhoneNum();
        String job = requestDto.getJob();
        user.addInfo(name, gitUrl, blogUrl, phoneNum, job);
        userRepository.save(user);

        //업데이트 한 정보를 다시 포트폴리오 정보에 넣기
        Portfolio portfolio = user.getPortfolio();
        portfolio.updateIntro(portfolio.getTitle(),gitUrl, portfolio.getIntroContents(), blogUrl);
        portfolioRepository.save(portfolio);

    }

    @Transactional
    public void updateInfo(AddInfoRequestDto request) {


        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User curUser = userQueryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        List<String> stackNames = request.getStack();

        if(stackNames.size()<3){
            throw new CustomException("기술 스택은 3개 이상 입력해 주세요",INVALID_INPUT_ERROR);
        }

        //기존의 스택 삭제하기
        userstackRepository.deleteAllUserStackByUserId(curUser.getId());

        //스텍 중복 삭제
        stackNames = stackNames.stream().distinct().collect(Collectors.toList());
        //입력으로 들어온 스택 추가
        stackNames.forEach(name -> {
            Stack stack = StackUtil.createStack(name, stackRepository);
            UserStack userStack = UserStack.create(curUser, stack);
            userstackRepository.save(userStack);
        });
        curUser.updateInfo(request.getName(), request.getPhoneNum(), request.getGitUrl(), request.getBlogUrl(), request.getJob());
        userRepository.save(curUser);

        //업데이트 한 정보를 다시 포트폴리오 정보에 넣기
        Portfolio portfolio = curUser.getPortfolio();
        portfolio.updateIntro(portfolio.getTitle(),curUser.getGithubUrl(), portfolio.getIntroContents(), curUser.getBlogUrl());
        portfolioRepository.save(portfolio);
    }

    public UserInfoResponseDto getUserInfo() {

        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //프로젝트 아이디 불러오기
        List<Integer> projectIds = projectRepository.findProjectIdByUserId(user.getId());

        //스택 내용 불러오기
        List<String> stackNames = userQueryRepository.findStackNamesByUserId(user.getId());

        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .projectId(projectIds)
                .stack(stackNames)
                .porfId(user.getPortfolio().getId())
                .email(user.getEmail())
                .phoneNum(user.getPhoneNum())
                .gitUrl(user.getGithubUrl())
                .blogUrl(user.getBlogUrl())
                .profileImage(user.getProfileImgUrl())
                .job(user.getJob())
                .build();
    }

    @Transactional
    public UserImageResponseDto updateProfile(MultipartFile multipartFile) {

        UserImageResponseDto userImageResponseDto = new UserImageResponseDto();

        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //현재 기본 이미지가 아니면 s3에서 삭제
        if (!user.getProfileImgUrl().equals("https://mini-project.s3.ap-northeast-2.amazonaws.com/profile/default.png")) {
            s3Uploader.deleteProfile(user.getProfileImgUrl(), 53);
        }
        try {
            String profileImgUrl = s3Uploader.upload(multipartFile, "profile");
            user.updateProfile(profileImgUrl);
            userImageResponseDto.setImg(profileImgUrl);
        } catch (IOException e) {
            //log.error("updateProfile -> s3upload : {}", e.getMessage());
            e.printStackTrace();
            throw new CustomImageException("사진 업로드에 실패하였습니다. 관리자에게 문의하세요.");
        }
        return userImageResponseDto;
    }

    @Transactional
    public void deleteProfile() {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(email).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //s3에서 삭제
        if (!user.getProfileImgUrl().equals("https://mini-project.s3.ap-northeast-2.amazonaws.com/profile/default.png")) {
            s3Uploader.deleteProfile(user.getProfileImgUrl(), 53);
        }

        user.deleteProfile();
    }


    public HttpHeaders tokenToHeader(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, tokenDto.getAccessToken());

        return headers;
    }
}