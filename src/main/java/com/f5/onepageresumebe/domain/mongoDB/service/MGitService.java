package com.f5.onepageresumebe.domain.mongoDB.service;

import com.f5.onepageresumebe.config.GitApiConfig;
import com.f5.onepageresumebe.domain.mongoDB.entity.MCommit;
import com.f5.onepageresumebe.domain.mongoDB.entity.MFile;
import com.f5.onepageresumebe.web.dto.MGit.request.MGitRequestDto;
import com.f5.onepageresumebe.web.dto.MGit.response.MCommitMessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
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
    private final GitApiConfig gitApiConfig;

    public void sync(MGitRequestDto requestDto) {
        GitHub gitHub = gitApiConfig.gitHub();

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
}