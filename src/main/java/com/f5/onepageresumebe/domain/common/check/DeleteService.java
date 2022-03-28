package com.f5.onepageresumebe.domain.common.check;

import com.f5.onepageresumebe.domain.career.repository.CareerRepository;
import com.f5.onepageresumebe.domain.git.entity.GitCommit;
import com.f5.onepageresumebe.domain.git.entity.GitFile;
import com.f5.onepageresumebe.domain.git.entity.MCommit;
import com.f5.onepageresumebe.domain.git.repository.commit.GitCommitRepository;
import com.f5.onepageresumebe.domain.git.repository.file.GitFileRepository;
import com.f5.onepageresumebe.domain.portfolio.entity.PortfolioBookmark;
import com.f5.onepageresumebe.domain.portfolio.repository.PortfoiloBookmarkRepository;
import com.f5.onepageresumebe.domain.portfolio.repository.PortfolioStackRepository;
import com.f5.onepageresumebe.domain.portfolio.repository.portfolio.PortfolioRepository;
import com.f5.onepageresumebe.domain.project.entity.Project;
import com.f5.onepageresumebe.domain.project.entity.ProjectBookmark;
import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import com.f5.onepageresumebe.domain.project.repository.ProjectBookmarkRepository;
import com.f5.onepageresumebe.domain.project.repository.ProjectImgRepository;
import com.f5.onepageresumebe.domain.project.repository.ProjectStackRepository;
import com.f5.onepageresumebe.domain.project.repository.project.ProjectRepository;
import com.f5.onepageresumebe.domain.user.entity.User;
import com.f5.onepageresumebe.domain.user.repository.UserRepository;
import com.f5.onepageresumebe.domain.user.repository.stack.UserStackRepository;
import com.f5.onepageresumebe.exception.ErrorCode;
import com.f5.onepageresumebe.exception.customException.CustomAuthenticationException;
import com.f5.onepageresumebe.exception.customException.CustomAuthorizationException;
import com.f5.onepageresumebe.exception.customException.CustomException;
import com.f5.onepageresumebe.security.SecurityUtil;
import com.f5.onepageresumebe.util.GitUtil;
import com.f5.onepageresumebe.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.f5.onepageresumebe.exception.ErrorCode.INVALID_INPUT_ERROR;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteService {

    private final CheckOwnerService checkOwnerService;

    private final UserStackRepository userStackRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProjectImgRepository projectImgRepository;
    private final CareerRepository careerRepository;
    private final ProjectRepository projectRepository;
    private final GitFileRepository gitFileRepository;
    private final UserRepository userRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final GitCommitRepository gitCommitRepository;
    private final ProjectStackRepository projectStackRepository;
    private final PortfolioStackRepository portfolioStackRepository;
    private final PortfoiloBookmarkRepository portfoiloBookmarkRepository;

    private final MongoTemplate mongoTemplate;
    private final S3Uploader s3Uploader;

    public void deleteCareer(Integer careerId){

        //로그인한 유저 확인
        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //현재 커리어를 작성한 유저인지 확인
        Boolean exists = careerRepository.existsByCareerIdAndUserEmail(careerId, userEmail);
        if(exists){
            careerRepository.deleteById(careerId);
        }else{
            throw new CustomAuthorizationException("내가 작성한 직무 경험만 삭제할 수 있습니다");
        }
    }

    public void deleteFile(Integer projectId, Integer commitId, Integer fileId){

        checkFileInProject(fileId,commitId,projectId);

        gitFileRepository.deleteById(fileId);
    }

    private void checkFileInProject(Integer fileId,Integer commitId ,Integer projectId){

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        //나의 프로젝트인지 확인
        projectRepository.findByUserEmailAndProjectId(userEmail,projectId).orElseThrow(()->
                new CustomAuthorizationException("내가 작성한 프로젝트만 삭제할 수 있습니다."));

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

    public void deleteMCommits(String repoName, String repoOwner) {

        Query query = new Query(Criteria.where("repoName").is(repoName));
        query.addCriteria(Criteria.where("repoOwner").is(repoOwner));

        mongoTemplate.remove(query, MCommit.class);
    }

    public void deleteProjectBookmark(Integer projectId) {
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요"));
        Project project = projectRepository.getById(projectId);
        project.updateBookmarkCount(-1);

        projectBookmarkRepository.deleteByUserIdAndProjectId(user.getId(), projectId);
    }

    public void deleteProjectImg(Integer projectId, Integer imageId) {

        //나의 프로젝트일때만 가져오기
        boolean isMyProject = checkOwnerService.isMyProject(projectId);

        if(isMyProject){

            //프로젝트 이미지 불러오기
            ProjectImg projectImg = projectImgRepository.findById(imageId).orElseThrow(() ->
                    new CustomException("해당 이미지가 존재하지 않습니다.", INVALID_INPUT_ERROR));

            //s3에서 삭제
            s3Uploader.deleteProjectImages(new ArrayList<>(Collections.singletonList(projectImg)));

            //삭제
            projectImgRepository.deleteById(imageId);
        }else{
            throw new CustomAuthorizationException("내가 작성한 프로젝트의 이미지만 삭제할 수 있습니다.");
        }
    }

    public void deleteProject(Integer projectId) {

        //나의 프로젝트일때만 가져오기
        boolean isMyProject = checkOwnerService.isMyProject(projectId);

        if(isMyProject){

            Project project = projectRepository.findById(projectId).orElseThrow(() ->
                    new CustomAuthorizationException("존재하지 않는 프로젝트입니다."));

            String repoName = project.getGitRepoName();
            String repoOwner = GitUtil.getOwner(project.getGitRepoUrl());

            //mongodb에 저장된 리포지토리 내용 모두 삭제
            Query query = new Query(Criteria.where("repoName").is(repoName));
            query.addCriteria(Criteria.where("repoOwner").is(repoOwner));
            query.addCriteria(Criteria.where("projectId").is(projectId));

            mongoTemplate.remove(query, MCommit.class);

            //현재 프로젝트의 모든 커밋들 삭제
            for (Integer id : gitCommitRepository.findAllIdsByProjectId(projectId)) {
                deleteProjectTroubleShootings(projectId, id);
            }

            //프로젝트의 스택들 전부 삭제
            projectStackRepository.deleteAllByProjectId(projectId);

            List<ProjectImg> projectImgs = projectImgRepository.findAllByProjectId(projectId);

            //s3에서 사진 모두 삭제
            s3Uploader.deleteProjectImages(projectImgs);

            //저장된 이미지 url 모두 삭제
            projectImgRepository.deleteAllInBatch(projectImgs);

            //프로젝트 연결된 북마크 삭제
            projectBookmarkRepository.deleteByProjectId(projectId);

            //프로젝트 삭제
            projectRepository.deleteById(projectId);
        }else{
            throw new CustomAuthorizationException("내가 작성한 프로젝트만 삭제할 수 있습니다.");
        }


    }

    public void deleteProjectTroubleShootings(Integer projectId, Integer commitId) {

        //나의 프로젝트일때만 가져오기
        boolean isMyProject = checkOwnerService.isMyProject(projectId);

        if (isMyProject){
            //커밋이 현재프로젝트에 있는 커밋인지 확인
            gitCommitRepository.findByProjectIdAndCommitId(projectId, commitId).orElseThrow(() ->
                    new CustomException("현재 프로젝트 내의 트러블 슈팅만 삭제할 수 있습니다.", INVALID_INPUT_ERROR));

            //깃 파일 삭제
            gitFileRepository.deleteByCommitId(commitId);

            //커밋 삭제
            gitCommitRepository.deleteById(commitId);
        }else{
            throw new CustomAuthorizationException("내가 작성한 트러블 슈팅만 삭제할 수 있습니다.");
        }

    }

    public void deleteUser() {

        String userEmail = SecurityUtil.getCurrentLoginUserId();

        User curUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));
        List<PortfolioBookmark> portfolioBookmarkList = curUser.getPortfolioBookmarkList();
        List<ProjectBookmark> projectBookmarkList = curUser.getProjectBookmarkList();

        //유저가 북마크중인 프로젝트 북마크 삭제
        for(ProjectBookmark projectBookmark : projectBookmarkList) {
            projectBookmarkRepository.deleteByUserIdAndProjectId(curUser.getId(), projectBookmark.getProject().getId());
        }

        //유저가 북마크중인 포트폴리오 북마크 삭제
        for (PortfolioBookmark portfolioBookmark : portfolioBookmarkList) {
            portfoiloBookmarkRepository.deleteByUserIdAndPortfolioId(portfolioBookmark.getUser().getId(),portfolioBookmark.getPortfolio().getId());
        }

        //유저가 가지고있는 포트폴리오의 스택 삭제
        portfolioStackRepository.deleteAllByPorfId(curUser.getPortfolio().getId());

        List<Project> projectList = curUser.getProjectList();

        //유저가 가지고 있는 프로젝트 삭제, 프로젝트 내에 stack, img, 트러블슈팅 삭제
        for(Project project : projectList) {
            deleteProject(project.getId());
        }

        //유저가 가지고 있는 포트폴리오의 커리어 테이블 전부 삭제
        careerRepository.deleteAllByPorfId(curUser.getPortfolio().getId());

        //유저와 연결된 userStack 전부 삭제
        userStackRepository.deleteAllUserStackByUserId(curUser.getId());
        //유저가 가지고 있는 포트폴리오 삭제
        portfolioRepository.deleteById(curUser.getPortfolio().getId());

        //유저 프로필 이미지 삭제
        s3Uploader.deleteProfile(curUser.getProfileImgUrl(), 53);

        //유저 정보 삭제
        userRepository.deleteById(curUser.getId());
    }

    public void deleteGitToken() {

        //현재 로그인 유저
        String userEmail = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //토큰 값 초기화
        user.setGitToken(null);
    }

    public void deleteUserProfile() {

        //현재 로그인 유저
        String email = SecurityUtil.getCurrentLoginUserId();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new CustomAuthenticationException("로그인 정보가 잘못되었습니다. 다시 로그인 해주세요."));

        //s3에서 삭제
        if (!user.getProfileImgUrl().equals("empty")) {
            s3Uploader.deleteProfile(user.getProfileImgUrl(), 53);
        }

        //기본이미지로 변경
        user.deleteProfile();
    }

}
