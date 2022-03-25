package com.f5.onepageresumebe.domain.git.service;

import com.f5.onepageresumebe.domain.git.entity.GitCommit;
import com.f5.onepageresumebe.domain.git.entity.GitFile;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.git.repository.commit.GitCommitRepository;
import com.f5.onepageresumebe.domain.git.repository.file.GitFileRepository;
import com.f5.onepageresumebe.domain.project.service.ProjectService;
import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.util.GitUtil;
import com.f5.onepageresumebe.web.git.dto.CommitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GitService {

    private final ProjectService projectService;
    private final GitCommitRepository gitCommitRepository;
    private final GitFileRepository gitFileRepository;

    @Transactional
    public CommitDto.IdResponse createTroubleShooting(Integer projectId, CommitDto.Request request) {

        //나의 프로젝트이면 가져옴
        Project project = projectService.getProjectIfMyProject(projectId,"나의 프로젝트에만 작성할 수 있습니다.");

        //프로젝트에 이미 등록된 커밋인지 체크
        GitCommit gitCommit = gitCommitRepository.findByShaAndProjectId(request.getSha(),projectId)
                .orElseGet(()->gitCommitRepository.save(new GitCommit(request.getCommitMessage(), request.getSha(), request.getTsName(), project)));

        //파일들을 커밋과 엮어 저장
        request.getTsFile().forEach(curFile->{
            GitFile gitFile = GitFile.create(curFile.getFileName(), GitUtil.combinePatchCode(curFile.getPatchCode()), curFile.getTsContent(), gitCommit);
            gitFileRepository.save(gitFile);
        });

        gitCommitRepository.save(gitCommit);

        return CommitDto.IdResponse.builder()
                .commitId(gitCommit.getId())
                .build();
    }

    @Transactional
    public void deleteFile(Integer projectId, Integer commitId, Integer fileId){

        checkFileInProject(fileId,commitId,projectId);

        gitFileRepository.deleteById(fileId);
    }

    @Transactional
    public void updateProjectTroubleShootings(Integer projectId, Integer commitId, CommitDto.Request request) {

        // 프로젝트에 연결된 특정 commitId내용들 전부 삭제
        projectService.deleteProjectTroubleShootings(projectId, commitId);

        //커밋 추가(트러블슈팅 내용까지)
        createTroubleShooting(projectId, request);
    }

    private void checkFileInProject(Integer fileId,Integer commitId ,Integer projectId){

        //나의 프로젝트인지 확인
        Project project = projectService.getProjectIfMyProject(projectId,"나의 프로젝트의 파일만 삭제할 수 있습니다.");

        GitFile gitFile = gitFileRepository.findFileByIdFetchAll(fileId).orElseThrow(() ->
                new CustomException("존재하지 않는 파일입니다.", ErrorCode.NOT_EXIST_ERROR));

        GitCommit gitCommit = gitFile.getCommit();
        Integer gitCommitId = gitCommit.getId();
        Integer gitProjectId = gitCommit.getProject().getId();

        //현재 프로젝트의 파일인지 확인
        if(gitCommitId != commitId || gitProjectId != projectId){
            throw new CustomException("현재 프로젝트의 파일만 삭제할 수 있습니다.",ErrorCode.INVALID_INPUT_ERROR);
        }
    }
}

