package com.yir.approvalhub.repository;

import com.yir.approvalhub.entity.Application;
import com.yir.approvalhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicantOrderByCreatedAtDesc(User applicant);
    List<Application> findByStatusOrderByCreatedAtDesc(Application.ApplicationStatus status);
}
