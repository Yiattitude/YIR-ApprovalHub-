package com.yir.approvalhub.service;

import com.yir.approvalhub.dto.ApprovalDecisionRequest;
import com.yir.approvalhub.entity.*;
import com.yir.approvalhub.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);

    private final ApprovalTaskRepository approvalTaskRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    public ApprovalService(ApprovalTaskRepository approvalTaskRepository,
                          ApplicationRepository applicationRepository,
                          UserRepository userRepository,
                          ApprovalHistoryRepository approvalHistoryRepository) {
        this.approvalTaskRepository = approvalTaskRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.approvalHistoryRepository = approvalHistoryRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<ApprovalTask> getMyPendingTasks() {
        User currentUser = getCurrentUser();
        return approvalTaskRepository.findByApproverAndStatusOrderByCreatedAtDesc(
                currentUser, ApprovalTask.TaskStatus.PENDING);
    }

    public List<ApprovalTask> getApplicationTasks(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return approvalTaskRepository.findByApplicationOrderByStepOrderAsc(application);
    }

    @Transactional
    public ApprovalTask processApprovalTask(Long taskId, ApprovalDecisionRequest request) {
        ApprovalTask task = approvalTaskRepository.findByIdWithApplication(taskId)
                .orElseThrow(() -> new RuntimeException("Approval task not found"));

        User currentUser = getCurrentUser();
        if (!task.getApprover().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only process your own approval tasks");
        }

        if (task.getStatus() != ApprovalTask.TaskStatus.PENDING) {
            throw new RuntimeException("This task has already been processed");
        }

        Application application = task.getApplication();
        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            throw new RuntimeException("This application is not in pending status");
        }

        // Update task
        ApprovalTask.TaskAction action = ApprovalTask.TaskAction.valueOf(request.getAction());
        task.setAction(action);
        task.setStatus(action == ApprovalTask.TaskAction.APPROVE ? 
                ApprovalTask.TaskStatus.APPROVED : ApprovalTask.TaskStatus.REJECTED);
        task.setComment(request.getComment());
        task.setProcessedAt(LocalDateTime.now());
        ApprovalTask savedTask = approvalTaskRepository.save(task);

        // Create approval history
        createHistory(application, 
                action == ApprovalTask.TaskAction.APPROVE ? 
                        ApprovalHistory.HistoryAction.APPROVED : ApprovalHistory.HistoryAction.REJECTED,
                task.getStepName(),
                request.getComment());

        // Update application status based on approval result
        if (action == ApprovalTask.TaskAction.REJECT) {
            // If rejected, mark application as rejected
            application.setStatus(Application.ApplicationStatus.REJECTED);
            application.setRejectionReason(request.getComment());
            application.setCompletedAt(LocalDateTime.now());
            logger.info("申请 {} 被拒绝，状态更新为 REJECTED", application.getId());

            // Skip remaining pending tasks
            List<ApprovalTask> remainingTasks = approvalTaskRepository
                    .findByApplicationOrderByStepOrderAsc(application);
            remainingTasks.stream()
                    .filter(t -> t.getStatus() == ApprovalTask.TaskStatus.PENDING)
                    .forEach(t -> {
                        t.setStatus(ApprovalTask.TaskStatus.SKIPPED);
                        approvalTaskRepository.save(t);
                    });
        } else {
            // Check if this is the last approval step
            List<ApprovalTask> allTasks = approvalTaskRepository
                    .findByApplicationOrderByStepOrderAsc(application);
            boolean allApproved = allTasks.stream()
                    .allMatch(t -> t.getStatus() == ApprovalTask.TaskStatus.APPROVED);

            logger.info("申请 {} 的所有审批任务: 已批准={}, 总数={}", application.getId(), allApproved, allTasks.size());
            
            if (allApproved) {
                application.setStatus(Application.ApplicationStatus.APPROVED);
                application.setCompletedAt(LocalDateTime.now());
                logger.info("申请 {} 所有审批已完成，状态更新为 APPROVED", application.getId());
            }
        }

        applicationRepository.save(application);

        return savedTask;
    }

    private void createHistory(Application application, ApprovalHistory.HistoryAction action, 
                               String stepName, String comment) {
        User currentUser = getCurrentUser();
        ApprovalHistory history = new ApprovalHistory();
        history.setApplication(application);
        history.setUser(currentUser);
        history.setAction(action);
        history.setStepName(stepName);
        history.setComment(comment);
        approvalHistoryRepository.save(history);
    }
}
