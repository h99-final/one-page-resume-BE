package com.f5.onepageresumebe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitConfig {
    @Value("${git.publicToken}")
    private String publicToken;

    public String getPublicToken() {
        return publicToken;
    }
}
