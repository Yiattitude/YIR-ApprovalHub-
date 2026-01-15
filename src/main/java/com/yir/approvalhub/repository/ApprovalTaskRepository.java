package com.yir.approvalhub.repository;

import com.yir.approvalhub.entity.ApprovalTask;
import com.yir.approvalhub.entity.Application;
import com.yir.approvalhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalTaskRepository extends JpaRepository<ApprovalTask, Long> {
    @Query("SELECT t FROM ApprovalTask t JOIN FETCH t.application a JOIN FETCH a.applicant WHERE t.approver = :approver AND t.status = :status ORDER BY t.createdAt DESC")
    List<ApprovalTask> findByApproverAndStatusOrderByCreatedAtDesc(User approver, ApprovalTask.TaskStatus status);
    
    @Query("SELECT t FROM ApprovalTask t JOIN FETCH t.application a JOIN FETCH a.applicant WHERE t.application = :application ORDER BY t.stepOrder ASC")
    List<ApprovalTask> findByApplicationOrderByStepOrderAsc(Application application);
    
    @Query("SELECT t FROM ApprovalTask t JOIN FETCH t.application a JOIN FETCH a.applicant WHERE t.id = :id")
    Optional<ApprovalTask> findByIdWithApplication(Long id);
}
