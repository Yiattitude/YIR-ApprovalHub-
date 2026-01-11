package com.yir.approvalhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApprovalHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApprovalHubApplication.class, args);
    }
}
