package com.f5.onepageresumebe.domain.service;

import com.f5.onepageresumebe.config.GitApiConfig;
import com.f5.onepageresumebe.domain.entity.GitCommit;
import com.f5.onepageresumebe.domain.entity.GitFile;
import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.domain.repository.GitCommitRepository;
import com.f5.onepageresumebe.domain.repository.GitFileRepository;
import com.f5.onepageresumebe.web.dto.gitCommit.requestDto.CommitRequestDto;
import com.f5.onepageresumebe.web.dto.gitCommit.responseDto.CommitMessageResponseDto;
import com.f5.onepageresumebe.web.dto.gitFile.requestDto.FileRequestDto;
import com.f5.onepageresumebe.web.dto.gitFile.responseDto.FilesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GitService {

    private final ProjectService projectService;
    private final GitCommitRepository gitCommitRepository;
    private final GitFileRepository gitFileRepository;
    private final GitApiConfig gitApiConfig;

    @Transactional
    public void createTroubleShooting(Integer projectId, CommitRequestDto request) {
        Project project = projectService.getProject(projectId);

        GitCommit gitCommit = gitCommitRepository.save(new GitCommit(request.getCommitMessage(), request.getSha(), request.getTsName(), project));

        List<FileRequestDto> fileRequestDtoList = request.getTsFile();

        for(FileRequestDto curFile : fileRequestDtoList) {
            GitFile gitFile = GitFile.create(curFile.getFileName(), curFile.getPatchCode(), curFile.getTsContent(), gitCommit);
            gitFileRepository.save(gitFile);
        }
    }

    public List<CommitMessageResponseDto> getCommitMessages(Integer projectId) {

//        Project project = projectService.getProject(projectId);
//
//        String gitUrl = project.getGitRepoUrl();
//        String repo = project.getGitRepoName();

        String gitUrl = "https://github.com/skekq123";
        String repo = "OurWiki";

        //repoName을 위의 형태로 받을지? 그게아니라면 git의 정보에서 사용자 id를 빼와 repoName앞에 붙여주기
        String repoName = makeRepoName(gitUrl, repo);

        List<CommitMessageResponseDto> commitMessageResponseDtoList = new ArrayList<>();

        GitHub gitHub = gitApiConfig.gitHub();

        try {
            GHRepository ghRepository = gitHub.getRepository(repoName);
            List<GHCommit> commits = ghRepository.listCommits().toList();
            for(GHCommit curCommit  : commits) {
                String curSha = curCommit.getSHA1();
                String curMessage = curCommit.getCommitShortInfo().getMessage();

                CommitMessageResponseDto curDto = new CommitMessageResponseDto(curSha, curMessage);
                commitMessageResponseDtoList.add(curDto);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("해당 repository가 존재하지 않습니다.");
        }

        return commitMessageResponseDtoList;
    }

    public List<FilesResponseDto> getFiles(Integer projectId, String sha) {
//        Project project = projectService.getProject(projectId);
//        String gitUrl = project.getGitRepoUrl();
//        String repoName = project.getGitRepoName();

        String gitUrl = "https://github.com/skekq123";
        String repo = "OurWiki";

        //repoName을 위의 형태로 받을지? 그게아니라면 git의 정보에서 사용자 id를 빼와 repoName앞에 붙여주기
        String repoName = makeRepoName(gitUrl, repo);

        List<FilesResponseDto> filesResponseDtoList = new ArrayList<>();

        GitHub gitHub = gitApiConfig.gitHub();

        try {
            GHRepository ghRepository = gitHub.getRepository(repoName);
            GHCommit commit = ghRepository.getCommit(sha);

            List<GHCommit.File> files = commit.getFiles();
            for (GHCommit.File curFile : files) {
                FilesResponseDto curDto = new FilesResponseDto(curFile.getFileName(), curFile.getPatch());
                filesResponseDtoList.add(curDto);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("해당 repository가 존재하지 않습니다.");
        }

        return filesResponseDtoList;
    }

    public String makeRepoName(String gitUrl, String reName) {
        int idx = gitUrl.indexOf(".com/");
        return gitUrl.substring(idx+5, gitUrl.length()) + "/" +  reName;
    }
}
