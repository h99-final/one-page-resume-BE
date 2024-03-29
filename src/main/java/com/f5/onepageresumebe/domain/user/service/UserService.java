package com.f5.onepageresumebe.domain.user.service;

import com.f5.onepageresumebe.util.S3Uploader;
import com.f5.onepageresumebe.domain.stack.entity.Stack;
import com.f5.onepageresumebe.domain.stack.repository.StackRepository;
import com.f5.onepageresumebe.domain.user.entity.Certification;
import com.f5.onepageresumebe.domain.user.repository.CertificationRepository;
import com.f5.onepageresumebe.domain.portfolio.entity.Portfolio;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepository;
import com.f5.onepageresumebe.domain.project.repository.project.ProjectRepository;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.domain.user.entity.UserStack;
import com.f5.onepageresumebe.domain.user.repository.UserRepository;
import com.f5.onepageresumebe.domain.user.repository.stack.UserStackRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.security.jwt.TokenProvider;
import com.f5.onepageresumebe.util.AES256;
import com.f5.onepageresumebe.util.UserUtil;
import com.f5.onepageresumebe.web.jwt.dto.TokenDto;
import com.f5.onepageresumebe.web.user.dto.UserDto;
import com.f5.onepageresumebe.web.stack.dto.StackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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
import java.util.Random;
import java.util.stream.Collectors;

import static com.f5.onepageresumebe.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;
import static com.f5.onepageresumebe.security.jwt.TokenProvider.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final AES256 aes256;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final S3Uploader s3Uploader;
    private final JavaMailSender javaMailSender;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserRepository userRepository;
    private final UserStackRepository userstackRepository;

    private final ProjectRepository projectRepository;

    private final PortfolioRepository portfolioRepository;

    private final StackRepository stackRepository;

    private final CertificationRepository certificationRepository;

    @Transactional
    public void registerUser(UserDto.SignUpRequest requestDto) {

        String email = requestDto.getEmail();

        //비밀번호, 비밀번호 확인 체크
        checkPassword(requestDto.getPassword(), requestDto.getPasswordCheck());

        // 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 유저 생성
        User user = User.create(email, password, null, null, null);

        //포트폴리오 생성 및 유저와 연결
        Portfolio portfolio = Portfolio.create(user);
        user.setPortfolio(portfolio);

        //저장
        userRepository.save(user);
        portfolioRepository.save(portfolio);

    }

    public boolean checkEmail(UserDto.EmailRequest request) {

        boolean res = false;

        //이미 존재한다면 TRUE
        Optional<User> found = userRepository.findByEmail(request.getEmail());
        if (found.isPresent()) res = true;

        return res;
    }

    public UserDto.LoginResult login(UserDto.LoginRequest loginDto) {

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
                .orElseThrow(() -> new CustomAuthenticationException("Email을 확인해 주세요"));

        //첫 로그인 유저가 아닐때
        boolean isFirstLogin = user.getCreatedAt().equals(user.getUpdatedAt());

        return UserDto.LoginResult.builder()
                .tokenDto(tokenDto)
                .loginResponseDto(UserDto.LoginResponse.builder()
                        .isFirstLogin(isFirstLogin)
                        .build())
                .build();
    }


    // 추가기입
    @Transactional
    public void addInfo(UserDto.AddInfoRequest requestDto) {

        //현재 로그인 유저 이메일 가져오기
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //유저 정보 가져오기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        List<String> stacks = requestDto.getStack();

        //스택 검증
        checkStack(stacks);

        //스택과ㅏ 유저 연결
        insertStacksInUser(user, stacks);

        // 추가 기입한 정보 유저에 넣기
        user.addInfo(requestDto.getName(), requestDto.getGitUrl(), requestDto.getBlogUrl(), requestDto.getPhoneNum(), requestDto.getJob());
        userRepository.save(user);

        //업데이트 한 정보를 다시 포트폴리오 정보에 넣기
        Portfolio portfolio = user.getPortfolio();
        portfolio.updateIntro(portfolio.getTitle(), requestDto.getGitUrl(), portfolio.getIntroContents(), requestDto.getBlogUrl());
        portfolioRepository.save(portfolio);

    }

    @Transactional
    public void updateInfo(UserDto.UpdateInfoRequest request) {

        //현재 로그인 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User curUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //업데이트
        curUser.updateInfo(request.getName(), request.getPhoneNum(), request.getGitUrl(), request.getBlogUrl(), request.getJob());

        //업데이트 한 정보를 다시 포트폴리오 정보에 넣기(유저 정보를 포트폴리오에서도 수정할 수 있음)
        Portfolio portfolio = curUser.getPortfolio();
        portfolio.updateIntro(portfolio.getTitle(), curUser.getGithubUrl(), portfolio.getIntroContents(), curUser.getBlogUrl());

        //저장
        portfolioRepository.save(portfolio);
        userRepository.save(curUser);
    }

    @Transactional
    public void updateStacks(StackDto request) {

        //현재 로그인 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User curUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        List<String> stackNames = request.getStack();

        //스택 검증
        checkStack(stackNames);

        //기존의 스택 삭제하기
        userstackRepository.deleteAllUserStackByUserId(curUser.getId());

        //스텍 중복 삭제
        stackNames = stackNames.stream().distinct().collect(Collectors.toList());

        //입력으로 들어온 스택 추가
        insertStacksInUser(curUser, stackNames);
    }

    @Transactional
    public void updateGitToken(String token) {

        //깃허브와 연결 테스트
        gitConnectionTest(token);

        //현재 로그인 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //토큰 암호화
        try {
            user.setGitToken(aes256.encrypt(token));
        } catch (Exception e) {
            log.error("git token 암호화에 실패하였습니다.");
            throw new CustomException("git token을 저장하던 중 오류가 발생하였습니다. 다시 시도해 주세요", INTERNAL_SERVER_ERROR);
        }

    }

    public UserDto.InfoResponse getUserInfo() {

        //현재 로그인 유저
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //프로젝트 아이디 불러오기
        List<Integer> projectIds = projectRepository.findProjectIdByUserId(user.getId());

        //스택 내용 불러오기
        List<String> stackNames = userstackRepository.findStackNamesByUserId(user.getId());

        //포트폴리오 불러오기
        Portfolio portfolio = user.getPortfolio();

        return UserDto.InfoResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .projectId(projectIds)
                .stack(stackNames)
                .porfId(portfolio.getId())
                .email(UserUtil.convertUserEmail(user.getEmail(),user.getIsKakao()))
                .phoneNum(user.getPhoneNum())
                .gitUrl(user.getGithubUrl())
                .blogUrl(user.getBlogUrl())
                .profileImage(user.getProfileImgUrl())
                .job(user.getJob())
                .porfShow(!portfolio.getIsTemp())
                .isToken(user.getGitToken() != null)
                .build();
    }

    @Transactional
    public UserDto.ImgResponse updateProfile(MultipartFile multipartFile) {

        UserDto.ImgResponse userImageResponseDto = UserDto.ImgResponse.builder().build();

        //현재 로그인 유저
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //현재 기본 이미지가 아니면 s3에서 삭제
        if (!user.getProfileImgUrl().equals("empty")) {
            s3Uploader.deleteProfile(user.getProfileImgUrl(), 53);
        }

        //이미지 업로드 후 url db에 저장
        String profileImgUrl = s3Uploader.uploadS3Ob(multipartFile, "profile");
        user.updateProfile(profileImgUrl);
        userImageResponseDto.setImg(profileImgUrl);
        return userImageResponseDto;
    }

    @Transactional
    public void ChangePassword(UserDto.PasswordRequest requestDto) {

        //현재 로그인 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User curUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        String inputCurPassword = requestDto.getCurPassword();

        //비밀번호를 맞게 입력하였는지 검증
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userEmail, inputCurPassword);
        authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);

        //비밀번호, 비밀번호 확인 체크
        checkPassword(requestDto.getPassword(), requestDto.getPasswordCheck());

        // 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());
        curUser.changePassword(password);
        userRepository.save(curUser);
    }

    @Transactional
    @Async("email-executor")
    public void certificationEmail(UserDto.EmailRequest requestDto) {

        //이메일 인증을 원하는 유저의 이메일
        String email = requestDto.getEmail();

        //랜덤값 부여
        String key = makeRandomString();

        //이메일 전송을 위한 메시지 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("인증번호 입력을 위한 메일 전송");
        message.setText("인증 번호 : " + key);

        javaMailSender.send(message);

        //db에 저장
        Certification certification = certificationRepository.findCertificationByEmail(email);

        //만약 기존의 이메일을 가진 certification 엔티티가 있으면
        if (certification != null) {
            //인증코드만 수정
            certification.changeCode(key);
        } else {
            //없으면, 새로 생성
            certification = Certification.create(email, key);
        }

        certificationRepository.save(certification);


    }

    @Transactional
    public boolean checkCertification(UserDto.CertificationRequest requestDto) {

        boolean isCertificated = false;

        String email = requestDto.getEmail();
        String code = requestDto.getCode();

        //db에 저장된 코드와 입력한 코드가 맞다면 entity가 존재
        Certification certification = certificationRepository.findCertificationByEmailAndCode(email, code);

        //해당 테이블이 있으면, return 데이터에 true 넣고 테이블 삭제
        if (certification != null) {
            isCertificated = true;
            certificationRepository.delete(certification);
        }
        return isCertificated;
    }

    public String makeRandomString() {
        Random random = new Random(); //난수 생성
        StringBuilder key = new StringBuilder(); // 인증번호

        for (int i = 0; i < 3; ++i) {
            int index = random.nextInt(25) + 65;

            key.append((char) index);
        }
        int numIndex = random.nextInt(9999) + 1000;
        key.append(numIndex);

        return key.toString();
    }

    @Transactional
    public void findPassword(UserDto.EmailRequest requestDto) {

        //새로운 랜덤 비밀번호
        String newPassword = makeRandomString();
        String email = requestDto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomAuthenticationException("해당 이메일이 존재하지 않습니다."));

        //임시 비밀번호 발송을 위한 메시지 작성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("임시 비밀번호 발송");
        message.setText("임시 비밀번호 : " + newPassword);

        javaMailSender.send(message);

        //비밀번호 변경
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserDto.EmailResponse findEmail(UserDto.FindEmailRequest requestDto) {

        String name = requestDto.getName();
        String phoneNum = requestDto.getPhoneNum();

        User user = userRepository.findUserByNameAndPhoneNum(name, phoneNum);

        UserDto.EmailResponse findEmailResponseDto = UserDto.EmailResponse.builder().build();

        //이름과 핸드폰번호가 일치한다면 특정 글자를 제외한 글자를 보여준다 -> ***gjkw@naver.com
        if (user == null) {
            findEmailResponseDto.setEmail(null);
        } else {
            String email = user.getEmail();
            int idx = email.indexOf("@");

            //@기준으로, 이메일을 나눈다
            String emailFront = email.substring(0, idx);
            String emailBack = email.substring(idx);

            int frontHalfLength = emailFront.length() / 2;

            StringBuilder blindFront = new StringBuilder(emailFront);

            for (int i = 0; i < frontHalfLength; ++i) {
                blindFront.setCharAt(i, '*');
            }

            findEmailResponseDto.setEmail(blindFront + emailBack);
        }
        return findEmailResponseDto;
    }

    private void insertStacksInUser(User user, List<String> stacks) {

        for (String curStackName : stacks) {

            Stack stack = stackRepository.findFirstByName(curStackName).orElse(null);

            //존재하지 않는 스택이라면 스택 생성 후 저장
            if (stack == null) {
                stack = Stack.create(curStackName);
                stackRepository.save(stack);
            }
            //유저와 스택 연결
            UserStack userStack = UserStack.create(user, stack);

            userstackRepository.save(userStack);

        }
    }

    private void checkPassword(String password, String passwordCheck) {

        if (!password.equals(passwordCheck)) {
            throw new CustomException("비밀번호와 비밀번호 확인이 다릅니다", INVALID_INPUT_ERROR);
        }
    }

    private void checkStack(List<String> stacks) {

        if (stacks.size() < 3) {
            throw new CustomException("기술 스택은 3개 이상 입력해 주세요", INVALID_INPUT_ERROR);
        }
    }

    public HttpHeaders tokenToHeader(TokenDto tokenDto) {

        //토큰을 response header에 담음
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, tokenDto.getAccessToken());

        return headers;
    }

    private void gitConnectionTest(String gitToken) {

        //깃허브와 연결 확인
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(gitToken).build();
            gitHub.checkApiUrlValidity();

        } catch (IOException e) {
            log.error("깃허브 연결 실패 : {}", e.getMessage());
            throw new CustomException("입력하신 깃허브 토큰이 유효하지 않습니다. 확인후 다시 입력해 주세요.", INVALID_INPUT_ERROR);
        }
    }
}