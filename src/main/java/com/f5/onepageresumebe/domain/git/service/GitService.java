package com.f5.onepageresumebe.domain.git.service;

import com.f5.onepageresumebe.domain.git.entity.GitCommit;
import com.f5.onepageresumebe.domain.git.entity.GitFile;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.git.repository.GitCommitRepository;
import com.f5.onepageresumebe.domain.git.repository.GitFileRepository;
import com.f5.onepageresumebe.domain.git.repository.GitQueryRepository;
import com.f5.onepageresumebe.domain.project.service.ProjectService;
import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.util.GitUtil;
import com.f5.onepageresumebe.web.git.dto.CommitDto;
import com.f5.onepageresumebe.web.git.dto.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GitService {

    private final ProjectService projectService;
    private final GitCommitRepository gitCommitRepository;
    private final GitFileRepository gitFileRepository;
    private final GitQueryRepository gitQueryRepository;

    @Transactional
    public CommitDto.IdResponse createTroubleShooting(Integer projectId, CommitDto.Request request) {

        Project project = projectService.getProjectIfMyProject(projectId);

        if (project == null) throw new CustomAuthorizationException("나의 프로젝트에만 작성할 수 있습니다.");

        //프로젝트에 이미 등록된 커밋인지 체크
        GitCommit gitCommit = gitCommitRepository.findByShaAndProjectId(request.getSha(),projectId)
                .orElse(null);

        //이미 등록된 커밋
        if (gitCommit == null) {
            gitCommit = gitCommitRepository.save(new GitCommit(request.getCommitMessage(), request.getSha(), request.getTsName(), project));
        }

        List<FileDto.Request> fileRequestDtoList = request.getTsFile();

        for (FileDto.Request curFile : fileRequestDtoList) {
            GitFile gitFile = GitFile.create(curFile.getFileName(), GitUtil.combinePatchCode(curFile.getPatchCode()), curFile.getTsContent(), gitCommit);
            gitFileRepository.save(gitFile);
        }

        gitCommitRepository.save(gitCommit);

        return CommitDto.IdResponse.builder()
                .commitId(gitCommit.getId())
                .build();
    }

    @Transactional
    public void deleteFile(Integer projectId, Integer commitId, Integer fileId){

        Project project = projectService.getProjectIfMyProject(projectId);

        if (project == null) throw new CustomAuthorizationException("나의 프로젝트의 파일만 삭제할 수 있습니다.");

        GitFile gitFile = gitQueryRepository.findFileByIdFetchAll(fileId).orElseThrow(() ->
                new CustomException("존재하지 않는 파일입니다.", ErrorCode.INVALID_INPUT_ERROR));

        GitCommit gitCommit = gitFile.getCommit();
        Integer gitCommitId = gitCommit.getId();
        Integer gitProjectId = gitCommit.getProject().getId();

        if(gitCommitId != commitId || gitProjectId != projectId){
            System.out.println("commitId"+gitCommitId+"/"+commitId);
            System.out.println("projectId"+gitProjectId+"/"+projectId);
            throw new CustomException("현재 프로젝트의 파일만 삭제할 수 있습니다.",ErrorCode.INVALID_INPUT_ERROR);
        }

        gitFileRepository.deleteById(fileId);
    }

//    public List<CommitMessageResponseDto> getCommitMessages(Integer projectId) {
//
//        Project project = projectService.getProjectIfMyProject(projectId);
//
//        if(project == null) throw new CustomAuthorizationException("나의 프로젝트에만 작성할 수 있습니다.");
//
//        String gitUrl = project.getGitRepoUrl(); //github.com/skekq123
//        String repo = project.getGitRepoName(); // ourWiki
//
//        //repoName을 위의 형태로 받을지? 그게아니라면 git의 정보에서 사용자 id를 빼와 repoName앞에 붙여주기
//        String repoName = makeRepoName(gitUrl, repo);
//
//        List<CommitMessageResponseDto> commitMessageResponseDtoList = new ArrayList<>();
//
//        GitHub gitHub = gitApiConfig.gitHub();
//
//        GHRepository ghRepository = null;
//
//        try{
//            ghRepository = gitHub.getRepository(repoName);
//        } catch (IOException e){
//            log.error("Repository 가져오기 실패: {}",e.getMessage());
//            e.printStackTrace();
//            throw new CustomException("Github Repository를 가져오는데 실패하였습니다.\n프로젝트를 만들 때 입력한 Repository 이름을 다시 한번 확인해 주세요.",
//                    ErrorCode.INVALID_INPUT_ERROR);
//        }
//
//        try {
//            List<GHCommit> commits = ghRepository.listCommits().toList();
//            for(GHCommit curCommit  : commits) {
//                String curSha = curCommit.getSHA1();
//                String curMessage = curCommit.getCommitShortInfo().getMessage();
//
//                CommitMessageResponseDto curDto = new CommitMessageResponseDto(curSha, curMessage);
//                commitMessageResponseDtoList.add(curDto);
//            }
//        } catch (IOException e) {
//            log.error("Commit 가져오기 실패: {}", e.getMessage());
//            e.printStackTrace();
//            throw new CustomException("Github Commit 정보를 불러오는데 실패하였습니다.\n 다시 시도해도 안될 경우, 관리자에게 문의해 주세요.",
//                    ErrorCode.INTERNAL_SERVER_ERROR);
//        }
//
//        return commitMessageResponseDtoList;
//    }

//    public List<FilesResponseDto> getFiles(Integer projectId, String sha) {
//
//
//        Project project = projectService.getProjectIfMyProject(projectId);
//        if(project == null) throw new CustomAuthorizationException("나의 프로젝트에만 작성할 수 있습니다.");
//
//        //하나의 커밋에는 하나의 트러블 슈팅만 작성할 수 있으므로, 이미 등록된 sha(commit)이면 에러 발생
//        GitCommit existCommit = gitCommitRepository.findBySha(sha);
//        if(existCommit != null){
//            throw new CustomException("이미 등록된 트러블 슈팅(commit)입니다. 기존의 트러블 슈팅을 수정해 주세요.",ErrorCode.DUPLICATED_INPUT_ERROR);
//        }
//
//        String gitUrl = project.getGitRepoUrl();
//        String repo = project.getGitRepoName();
//
//        //repoName을 위의 형태로 받을지? 그게아니라면 git의 정보에서 사용자 id를 빼와 repoName앞에 붙여주기
//        String repoName = makeRepoName(gitUrl, repo);
//
//        List<FilesResponseDto> filesResponseDtoList = new ArrayList<>();
//
//        GitHub gitHub = gitApiConfig.gitHub();
//
//        GHRepository ghRepository = null;
//
//        try{
//            ghRepository = gitHub.getRepository(repoName);
//        } catch (IOException e){
//            log.error("Repository 가져오기 실패: {}",e.getMessage());
//            e.printStackTrace();
//            throw new CustomException("Github Repository를 가져오는데 실패하였습니다.\n프로젝트를 만들 때 입력한 Repository 이름을 다시 한번 확인해 주세요.",
//                    ErrorCode.INVALID_INPUT_ERROR);
//        }
//
//        try {
//            GHCommit commit = ghRepository.getCommit(sha);
//
//            List<GHCommit.File> files = commit.getFiles();
//            for (GHCommit.File curFile : files) {
//                List<String> patchCodeList = GitUtil.parsePatchCode(curFile.getPatch());
//                FilesResponseDto curDto = new FilesResponseDto(curFile.getFileName(), patchCodeList);
//                filesResponseDtoList.add(curDto);
//            }
//        } catch (IOException e) {
//            log.error("File 가져오기 실패: {}", e.getMessage());
//            e.printStackTrace();
//            throw new CustomException("Github File 정보를 불러오는데 실패하였습니다.\n 다시 시도해도 안될 경우, 관리자에게 문의해 주세요.",
//                    ErrorCode.INTERNAL_SERVER_ERROR);
//        }
//
//        return filesResponseDtoList;
//    }


    @Transactional
    public void updateProjectTroubleShootings(Integer projectId, Integer commitId, CommitDto.Request request) {

        // 프로젝트에 연결된 특정 commitId내용들 전부 삭제
        projectService.deleteProjectTroubleShootings(projectId, commitId);

        //커밋 추가(트러블슈팅 내용까지)
        createTroubleShooting(projectId, request);
    }
}

