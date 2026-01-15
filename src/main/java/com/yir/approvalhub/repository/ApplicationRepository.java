package com.yir.approvalhub.repository;

import com.yir.approvalhub.entity.Application;
import com.yir.approvalhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.applicant WHERE a.applicant = :applicant ORDER BY a.createdAt DESC")
    List<Application> findByApplicantOrderByCreatedAtDesc(User applicant);
    
    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.applicant WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<Application> findByStatusOrderByCreatedAtDesc(Application.ApplicationStatus status);
}
