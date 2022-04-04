package com.f5.onepageresumebe.domain.git.service;

import com.f5.onepageresumebe.config.GitConfig;
import com.f5.onepageresumebe.domain.common.check.DeleteService;
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
import com.f5.onepageresumebe.web.git.dto.RepoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static com.f5.onepageresumebe.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MGitService {

    private final GitConfig gitConfig;

    private final DeleteService deleteService;

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

        deleteService.deleteMCommits(repoName, repoOwner,projectId);
        try {
            final GHRepository ghRepository = getGitHub().getRepository(makeRepoName(repoUrl, repoName));

            CompletableFuture.runAsync(()->{
                try {
                    taskRepository.save(projectId, false);
                    List<GHCommit> commits = ghRepository.listCommits().toList();

                    //현재 가져오고자 하는 프로젝트 id, 커밋 갯수 저장
                    taskRepository.saveCommitCount(projectId, commits.size());
                    ForkJoinPool myPool = new ForkJoinPool(15);
                    myPool.submit(()-> {
                        commits.parallelStream().forEach((commit)-> {
                            try {
                                log.info("parallelStream");
                                String curSha = commit.getSHA1();
                                String curMessage = commit.getCommitShortInfo().getMessage();

                                List<MFile> files = getFiles(ghRepository, curSha);

                                Date date = commit.getCommitDate();

                                MCommit mCommit = MCommit.create(projectId, date ,curMessage, curSha, repoName, repoOwner, files);
                                mongoTemplate.save(mCommit);

                            }catch (IOException e) {
                                e.printStackTrace();
                                log.error("Commit 정보 가져오기 실패 : {}",e.getMessage());
                                throw new CustomException("Commit을 불러오던 중 문제가 발생하였습니다. 잠시 후 다시 시도해 주세요",INTERNAL_SERVER_ERROR);
                            }
                        });
                        taskRepository.save(projectId, true);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("Commit 정보 가져오기 실패 : {}",e.getMessage());
                    throw new CustomException("Commit을 불러오던 중 문제가 발생하였습니다. 잠시 후 다시 시도해 주세요",INTERNAL_SERVER_ERROR);
                }
            }).exceptionally(t -> {
                t.printStackTrace();
                return null;
            }).thenAcceptAsync(s -> log.info("CORRECT value" + s));

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Repository 가져오기 실패: {}", e.getMessage());
            throw new CustomException("Repository를 불러오던 중 문제가 발생하였습니다. 잠시 후 다시 시도해 주세요",INTERNAL_SERVER_ERROR);
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
            e.printStackTrace();
            log.error("File 가져오기 실패: {}", e.getMessage());
            throw new CustomException("Commit의 파일을 불러오던 중 문제가 발생하였습니다. 잠시 후 다시 시도해 주세요",INTERNAL_SERVER_ERROR);
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (MCommit curCommit : mCommits) {
            CommitDto.MessageResponse mCommitMessageResponseDto = new CommitDto.MessageResponse(curCommit.getSha(), curCommit.getMessage(), dateFormat.format(curCommit.getDate()));
            mCommitMessageResponseDtos.add(mCommitMessageResponseDto);
        }

        return mCommitMessageResponseDtos;
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

    private String makeRepoName(String gitUrl, String reName) {
        int idx = gitUrl.indexOf(".com/");
        String res = "";
        if(idx != -1) res = gitUrl.substring(idx + 5) + "/" + reName;

        return res;
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

    public Boolean gitRepoValidation(RepoDto.Request request) {
        Boolean res = true;

        String repoUrl = request.getGitRepoUrl();
        String repoName = request.getGitRepoName();

        GitHub gitHub = null;

        try {
            gitHub = new GitHubBuilder().withOAuthToken(gitConfig.getPublicToken()).build();
            gitHub.checkApiUrlValidity();

        } catch (IOException e) {
            log.error("깃허브 연결 실패 : {}", e.getMessage());
            throw new CustomException("토큰 정보가 잘못되었습니다. 토큰을 재등록 해주세요.", INVALID_INPUT_ERROR);
        }

        try {
            GHRepository ghRepository = gitHub.getRepository(makeRepoName(repoUrl, repoName));
        } catch (IOException e) {
            res = false;
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            res = false;
            e.printStackTrace();
        }

        return res;
    }

    public List<CommitDto.MessageResponse> getCommitsByDate(Integer projectId, String date) throws ParseException {

        Integer year = Integer.valueOf(date.substring(0,4));
        Integer month = Integer.valueOf(date.substring(5,7));
        Integer day = Integer.valueOf(date.substring(8,10));

        DateTime startDateTime = new DateTime(year, month, day, 0,0,0);
        DateTime endDateTime = new DateTime(year, month, day, 23,59,59);

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
        query.addCriteria(Criteria.where("date").gte(startDateTime).lte(endDateTime));

        query.with(Sort.by(Sort.Order.desc("date")));

        List<MCommit> mCommits = mongoTemplate.find(query, MCommit.class);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (MCommit curCommit : mCommits) {
            CommitDto.MessageResponse mCommitMessageResponseDto = new CommitDto.MessageResponse(curCommit.getSha(), curCommit.getMessage(), dateFormat.format(curCommit.getDate()));
            mCommitMessageResponseDtos.add(mCommitMessageResponseDto);
        }

        return mCommitMessageResponseDtos;

    }
}
