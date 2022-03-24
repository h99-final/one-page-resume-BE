package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.domain.git.entity.MCommit;
import com.f5.onepageresumebe.domain.git.entity.MFile;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitRunnable implements Runnable {
    Integer start, end;
    List<GHCommit> commits;
    GHRepository ghRepository;
    String repoName, repoOwner;
    MongoTemplate mongoTemplate;

    public GitRunnable(Integer start, Integer end, List<GHCommit> commits, GHRepository ghRepository, String repoName, String repoOwner, MongoTemplate mongoTemplate ) {
        this.start = start;
        this.end = end;
        this.commits = commits;
        this.ghRepository = ghRepository;
        this.repoName = repoName;
        this.repoOwner = repoOwner;
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public void run() {
        try {
            for(int i = start; i < end; ++i) {
                GHCommit curCommit = commits.get(i);
                String curSha = curCommit.getSHA1();
                String curMessage = curCommit.getCommitShortInfo().getMessage();

                List<MFile> files = getFiles(ghRepository, curSha);

                MCommit mCommit = MCommit.create(i+1 ,curMessage, curSha, repoName, repoOwner, files);
                mongoTemplate.save(mCommit);
            }
        }catch (IOException e) {
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
            e.printStackTrace();
        }

        return files;
    }
}