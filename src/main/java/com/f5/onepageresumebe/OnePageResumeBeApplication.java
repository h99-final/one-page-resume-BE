package com.f5.onepageresumebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OnePageResumeBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnePageResumeBeApplication.class, args);
    }

}
