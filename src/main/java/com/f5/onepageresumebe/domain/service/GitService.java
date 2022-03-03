//package com.f5.onepageresumebe.domain.service;
//
//import com.f5.onepageresumebe.domain.entity.GitCommit;
//import com.f5.onepageresumebe.domain.entity.GitRepository;
//import com.f5.onepageresumebe.domain.entity.Project;
//import com.f5.onepageresumebe.domain.repository.*;
//import com.f5.onepageresumebe.web.dto.git.responseDto.CommitResponseDto;
//import com.f5.onepageresumebe.web.dto.git.responseDto.GetCommitResponseDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.kohsuke.github.GHRepository;
//import org.kohsuke.github.GitHub;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//@Slf4j
//public class GitService {
//
//    private final GitHub github;
//    private final UserRepository userRepository;
//    private final GitRepoRepository gitRepoRepository;
//    private final ProjectRepository projectRepository;
//    private final GitCommitRepository gitCommitRepository;
//    private final GitFileRepository gitFileRepository;
//
//    @Transactional
//    public Integer addRepository(Integer projectId, String repoName, String url) {
//
//        //깃허브에서 이름으로 리포지토리 가져오기 - 존재하는지 확인 용도
//        try {
//        GHRepository repository = github.getRepository(repoName);
//    } catch (IOException e) {
//        log.error("addRepository -> getRepository : {}", e.getMessage());
//        throw new IllegalArgumentException("깃허브에 해당 리포지토리가 존재하지 않습니다");
//    }
//
//    Project project = projectRepository.findById(projectId).orElseThrow(() ->
//            new IllegalArgumentException("해당 프로젝트가 존재하지 않습니다"));
//
//    GitRepository gitRepository = GitRepository.create(repoName, url, project);
//
//        gitRepoRepository.save(gitRepository);
//
//        return gitRepository.getId();
//    }
//
//    @Transactional
//    public Integer addCommit(String sha, String message, Integer repoId){
//
//        GitRepository gitRepository = gitRepoRepository.findById(repoId).orElseThrow(() ->
//                new IllegalArgumentException("저장되지 않은 리포지토리입니다"));
//
//
//        //sha, repositoryId로 커밋 조회(DB)
//        GitCommit gitCommit = gitCommitRepository.findByShaAndRepoId(sha,repoId).orElse(null);
//
//
//        if(gitCommit==null){
//            //현재 리포지토리에 존재하지 않는다면 생성하여 아이디 리턴
//            GitCommit commit = GitCommit.create(message, sha, gitRepository);
//            gitCommitRepository.save(commit);
//            return commit.getId();
//        }else{
//            //현재 리포지토리에 이미 존재하는 커밋(sha로 비교)이면 존재하는 커밋 아이디 리턴
//            return gitCommit.getId();
//        }
//    }
//
//    public GetCommitResponseDto getCommits(String repoName){
//
//        List<CommitResponseDto> commitResponseDtos = new ArrayList<>();
//
//        try {
//            GHRepository ghRepository = github.getRepository(repoName);
//            ghRepository.listCommits().toList().stream().forEach(ghCommit -> {
//                CommitResponseDto responseDto = null;
//                try {
//                    responseDto = CommitResponseDto.builder()
//                            .sha(ghCommit.getSHA1())
//                            .message(ghCommit.getCommitShortInfo().getMessage())
//                            .build();
//                } catch (IOException e) {
//                    log.error("getCommits -> stream : {}",e.getMessage());
//                    throw new IllegalArgumentException("깃허브에서 커밋을 가져오던 중 에러가 발생하였습니다");
//                }
//                commitResponseDtos.add(responseDto);
//            });
//
//            return GetCommitResponseDto.builder().commits(commitResponseDtos).build();
//
//        }catch (IOException e){
//            log.error("getCommits -> getRepository : {}",e.getMessage());
//            throw new IllegalArgumentException("깃허브에 해당 리포지토리가 존재하지 않습니다");
//        }
//    }
//
//    @Transactional
//    public void deleteCommit(Integer commitId){
//
//        //커밋 하위의 파일들을 조회
//        List<Integer> fileIds = gitFileRepository.findIdsByCommitId(commitId);
//
//        //모든 파일 삭제
//        gitFileRepository.deleteAllByIds(fileIds);
//
//        //커밋 삭제
//        gitCommitRepository.deleteById(commitId);
//    }
//
//
//}
