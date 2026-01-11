package com.yir.approvalhub.repository;

import com.yir.approvalhub.entity.ApprovalTask;
import com.yir.approvalhub.entity.Application;
import com.yir.approvalhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalTaskRepository extends JpaRepository<ApprovalTask, Long> {
    List<ApprovalTask> findByApproverAndStatusOrderByCreatedAtDesc(User approver, ApprovalTask.TaskStatus status);
    List<ApprovalTask> findByApplicationOrderByStepOrderAsc(Application application);
}
