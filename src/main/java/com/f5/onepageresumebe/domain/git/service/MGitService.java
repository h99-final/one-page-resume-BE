package com.f5.onepageresumebe.domain.git.service;

import com.f5.onepageresumebe.domain.git.entity.MCommit;
import com.f5.onepageresumebe.domain.git.entity.MFile;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.project.repository.project.ProjectRepository;
import com.f5.onepageresumebe.domain.apiCallCheck.service.ApiCallService;
import com.f5.onepageresumebe.domain.task.repository.TaskRepositoryImpl;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.domain.user.repository.UserRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.AES256;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;

import com.f5.onepageresumebe.util.GitUtil;
import com.f5.onepageresumebe.web.git.dto.CommitDto;
import com.f5.onepageresumebe.web.git.dto.FileDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;
import static com.f5.onepageresumebe.exception.ErrorCode.TOO_MANY_CALL;
import static com.f5.onepageresumebe.exception.ErrorCode.NOT_EXIST_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class MGitService {

    private final MongoTemplate mongoTemplate;
    private final AES256 aes256;
    private final ApiCallService apiCallService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepositoryImpl taskRepository;

    public void order(Integer projectId) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Project project = projectRepository.findByUserEmailAndProjectId(userEmail, projectId).orElseThrow(() ->
                new CustomAuthorizationException("내가 작성한 프로젝트에서만 가능합니다."));

        User user = project.getUser();

        //api를 호출할 수 있는지 체크한다.
        syncCallCheck(user.getId());

        //토큰이 없으면 바로 RETURN
        if(project.getUser().getGitToken()==null) return;

        String repoUrl = project.getGitRepoUrl();
        String repoName = project.getGitRepoName();
        String repoOwner = GitUtil.getOwner(repoUrl);

        //싱크를 맞추기 전, 같은 repoName, Owner의 커밋들이 있으면 db의 데이터 전체 삭제 후 추가 시작
        deleteMCommits(repoName, repoOwner);
        try {
            final GHRepository ghRepository = getGitHub().getRepository(makeRepoName(repoUrl, repoName));

            CompletableFuture.runAsync(()->{
                try {
                    taskRepository.save(projectId, false);
                    List<GHCommit> commits = ghRepository.listCommits().toList();
                    commits.parallelStream().forEach((commit)-> {
                        try {
                            log.info("parallerStream");
                            String curSha = commit.getSHA1();
                            String curMessage = commit.getCommitShortInfo().getMessage();

                            List<MFile> files = getFiles(ghRepository, curSha);

                            Date date = commit.getCommitDate();

                            MCommit mCommit = MCommit.create(projectId, date ,curMessage, curSha, repoName, repoOwner, files);
                            mongoTemplate.save(mCommit);

                        }catch (IOException e) {
                        }
                    });
                    taskRepository.save(projectId, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            log.error("Repository 가져오기 실패: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public List<MFile> getFiles(GHRepository ghRepository, String sha) {
        List<MFile> files = new ArrayList<>();

        try {
            GHCommit commit = ghRepository.getCommit(sha);

            List<GHCommit.File> curFiles = commit.getFiles();
            for (GHCommit.File curFile : curFiles) {
                MFile mFile = MFile.create(curFile.getFileName(), curFile.getPatch());
                files.add(mFile);
            }
        } catch (IOException e) {
            log.error("File 가져오기 실패: {}", e.getMessage());
            e.printStackTrace();
        }

        return files;
    }

    public List<CommitDto.MessageResponse> getCommits(Integer projectId) {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        Project project = projectRepository.findByUserEmailAndProjectId(userEmail, projectId).orElseThrow(() ->
                new CustomAuthorizationException("내가 작성한 프로젝트에서만 가능합니다."));

        String repoUrl = project.getGitRepoUrl();
        String repoName = project.getGitRepoName();
        String repoOwner = GitUtil.getOwner(repoUrl);

        List<CommitDto.MessageResponse> mCommitMessageResponseDtos = new ArrayList<>();

        Query query = new Query(Criteria.where("repoName").is(repoName));
        query.addCriteria(Criteria.where("repoOwner").is(repoOwner));
        query.addCriteria(Criteria.where("projectId").is(projectId));
        query.with(Sort.by(Sort.Order.desc("date")));

        List<MCommit> mCommits = mongoTemplate.find(query, MCommit.class);

        for (MCommit curCommit : mCommits) {
            CommitDto.MessageResponse mCommitMessageResponseDto = new CommitDto.MessageResponse(curCommit.getSha(), curCommit.getMessage());
            mCommitMessageResponseDtos.add(mCommitMessageResponseDto);
        }

        return mCommitMessageResponseDtos;
    }

    public void deleteMCommits(String repoName, String repoOwner) {

        Query query = new Query(Criteria.where("repoName").is(repoName));
        query.addCriteria(Criteria.where("repoOwner").is(repoOwner));

        mongoTemplate.remove(query, MCommit.class);
    }

    public List<FileDto.Response> findFilesBySha(String sha) {

        MCommit gitCommit = mongoTemplate.findOne(
                Query.query(Criteria.where("sha").is(sha)),
                MCommit.class
        );

        if (gitCommit == null) {
            throw new CustomException("해당 커밋의 파일들이 존재하지 않습니다.",NOT_EXIST_ERROR);
        }

        List<FileDto.Response> responseDtos = new ArrayList<>();
        gitCommit.getFiles().forEach(gitFile -> {

            String patchCode = gitFile.getPatchCode();
            if (patchCode != null) {
                responseDtos.add(
                        FileDto.Response.builder()
                                .name(gitFile.getName())
                                .patchCode(GitUtil.parsePatchCode(patchCode))
                                .build());
            }
        });

        return responseDtos;
    }

    private GitHub getGitHub() {

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));
        String rawToken = null;
        String encryptToken = user.getGitToken();

        try {
            rawToken = aes256.decrypt(user.getGitToken());
        } catch (Exception e) {
            log.error("git Token 복호화에 실패하였습니다.");
            throw new CustomException("토큰 정보가 잘못되었습니다. 토큰을 재등록 해주세요.", INVALID_INPUT_ERROR);
        }

        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(rawToken).build();
            gitHub.checkApiUrlValidity();

            return gitHub;
        } catch (IOException e) {
            log.error("깃허브 연결 실패 : {}", e.getMessage());
            throw new CustomException("토큰 정보가 잘못되었습니다. 토큰을 재등록 해주세요.", INVALID_INPUT_ERROR);
        }

    }

    public String makeRepoName(String gitUrl, String reName) {
        int idx = gitUrl.indexOf(".com/");
        return gitUrl.substring(idx + 5) + "/" + reName;
    }

    private void syncCallCheck(Integer userId){

        if(!apiCallService.callAvailability(userId)){
            log.error("호출 불가");
            throw new CustomException("깃허브 불러오기는 20초에 1번 가능합니다. 잠시 후에 다시 시도해 주세요",TOO_MANY_CALL);
        }else{
            log.info("호출");
            apiCallService.call(userId);
        }
    }

    public Boolean isCompletion(Integer projectId) {
        return taskRepository.findByProjectId(projectId);
    }
}
