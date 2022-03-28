package com.f5.onepageresumebe.domain.git.service;

import com.f5.onepageresumebe.domain.common.check.CheckOwnerService;
import com.f5.onepageresumebe.domain.common.check.DeleteService;
import com.f5.onepageresumebe.domain.git.entity.GitCommit;
import com.f5.onepageresumebe.domain.git.entity.GitFile;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.git.repository.commit.GitCommitRepository;
import com.f5.onepageresumebe.domain.git.repository.file.GitFileRepository;
import com.f5.onepageresumebe.domain.project.repository.project.ProjectRepository;
import com.f5.onepageresumebe.domain.project.service.ProjectService;
import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
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

    private final CheckOwnerService checkOwnerService;
    private final DeleteService deleteService;

    private final ProjectRepository projectRepository;
    private final GitCommitRepository gitCommitRepository;
    private final GitFileRepository gitFileRepository;

    @Transactional
    public CommitDto.IdResponse createTroubleShooting(Integer projectId, CommitDto.Request request) {

        //나의 프로젝트이면 가져옴
        boolean isMyProject = checkOwnerService.isMyProject(projectId);

        if (isMyProject){

            Project project = projectRepository.findById(projectId).orElseThrow(() ->
                    new CustomAuthorizationException("내가 작성한 프로젝트에만 작성할 수 있습니다."));

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
        }else{
            throw new CustomAuthorizationException("내가 작성한 프로젝트에만 작성할 수 있습니다.");
        }
    }

    @Transactional
    public void updateProjectTroubleShootings(Integer projectId, Integer commitId, CommitDto.Request request) {

        // 프로젝트에 연결된 특정 commitId내용들 전부 삭제
        deleteService.deleteProjectTroubleShootings(projectId, commitId);

        //커밋 추가(트러블슈팅 내용까지)
        createTroubleShooting(projectId, request);
    }

}

