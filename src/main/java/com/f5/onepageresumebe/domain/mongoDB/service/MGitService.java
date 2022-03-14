package com.f5.onepageresumebe.domain.mongoDB.service;

import com.f5.onepageresumebe.domain.mongoDB.entity.MCommit;
import com.f5.onepageresumebe.domain.mongoDB.entity.MFile;
import com.f5.onepageresumebe.domain.mysql.entity.User;
import com.f5.onepageresumebe.domain.mysql.repository.querydsl.UserQueryRepository;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.AES256;
import com.f5.onepageresumebe.util.GitUtil;
import com.f5.onepageresumebe.web.dto.MGit.request.MGitRequestDto;
import com.f5.onepageresumebe.web.dto.MGit.response.MCommitMessageResponseDto;
import com.f5.onepageresumebe.web.dto.gitFile.responseDto.FilesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MGitService {

    private final MongoTemplate mongoTemplate;
    private final UserQueryRepository userQueryRepository;
    private final AES256 aes256;

    public void sync(MGitRequestDto requestDto) {
        GitHub gitHub = getGitHub();

        String repoName = requestDto.getAccessRepoName();

        GHRepository ghRepository = null;

        //싱크를 맞추기 전, 같은 repoName, Owner의 커밋들이 있으면 db의 데이터 전체 삭제 후 추가 시작
        deleteMCommits(requestDto);

        try {
            ghRepository = gitHub.getRepository(repoName);
        } catch (IOException e) {
            log.error("Repository 가져오기 실패: {}",e.getMessage());
            e.printStackTrace();
        }

        try {
            List<GHCommit> commits = ghRepository.listCommits().toList();
            for(GHCommit curCommit  : commits) {
                String curSha = curCommit.getSHA1();
                String curMessage = curCommit.getCommitShortInfo().getMessage();

                List<MFile> files = getFiles(ghRepository, curSha);

                MCommit mCommit = MCommit.create(curMessage, curSha, requestDto, files);

                mongoTemplate.save(mCommit);
            }
        } catch (IOException e) {
            log.error("Commit 가져오기 실패: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    public List<MFile> getFiles(GHRepository ghRepository, String  sha) {
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

    public List<MCommitMessageResponseDto> getCommits(MGitRequestDto requestDto) {
        List<MCommitMessageResponseDto> mCommitMessageResponseDtos = new ArrayList<>();

        String repoName = requestDto.getRepoName();
        String repoOwner = requestDto.getOwner();

        Query query = new Query(Criteria.where("repoName").is(repoName));
        query.addCriteria(Criteria.where("repoOwner").is(repoOwner));

        List<MCommit> mCommits = mongoTemplate.find(query, MCommit.class);

        for(MCommit curCommit: mCommits) {
            MCommitMessageResponseDto mCommitMessageResponseDto = new MCommitMessageResponseDto(curCommit.getSha(), curCommit.getMessage());
            mCommitMessageResponseDtos.add(mCommitMessageResponseDto);
        }

        return mCommitMessageResponseDtos;
    }

    public void deleteMCommits(MGitRequestDto requestDto) {
        String repoName = requestDto.getRepoName();
        String repoOwner = requestDto.getOwner();

        Query query = new Query(Criteria.where("repoName").is(repoName));
        query.addCriteria(Criteria.where("repoOwner").is(repoOwner));

        mongoTemplate.remove(query, MCommit.class);
    }

    public List<FilesResponseDto> findFilesBySha(String sha){

        MCommit gitCommit = mongoTemplate.findOne(
                Query.query(Criteria.where("sha").is(sha)),
                MCommit.class
        );

        if(gitCommit==null){
            //todo: 오류 처리
        }

        List<FilesResponseDto> responseDtos = new ArrayList<>();
        gitCommit.getFiles().forEach(gitFile -> {

            String patchCode = gitFile.getPatchCode();
            if(patchCode!=null){
                responseDtos.add(new FilesResponseDto(gitFile.getName(), GitUtil.parsePatchCode(patchCode)));
            }
        });

        return responseDtos;
    }

    private GitHub getGitHub(){

        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userQueryRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));
        String rawToken = null;
        try{
            rawToken = aes256.decrypt(user.getGitToken());
        }catch (Exception e){
            log.error("git Token 복호화에 실패하였습니다.");
        }

        try{
            GitHub gitHub = new GitHubBuilder().withOAuthToken(rawToken).build();
            gitHub.checkApiUrlValidity();

            return gitHub;
        }catch (IOException e){
            log.error("깃허브 연결 실패 : {}",e.getMessage());
        }

        return null;
    }

}
