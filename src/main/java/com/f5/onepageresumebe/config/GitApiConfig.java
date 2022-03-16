package com.f5.onepageresumebe.config;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GitApiConfig {

    @Value("${git.oauthToken}")
    private String oauthToken;

    @Bean
    public GitHub gitHub(){

        try{
            GitHub gitHub = new GitHubBuilder().withOAuthToken(oauthToken).build();
            gitHub.checkApiUrlValidity();
            return gitHub;
        }catch (IOException e){
            log.error("깃허브 연결 실패 : {}",e.getMessage());
        }

        return null;
    }
}
