package com.yir.approvalhub.service;

import com.yir.approvalhub.dto.*;
import com.yir.approvalhub.entity.*;
import com.yir.approvalhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private ReimbursementApplicationRepository reimbursementApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApprovalTaskRepository approvalTaskRepository;

    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public LeaveApplication createLeaveApplication(LeaveApplicationRequest request) {
        User applicant = getCurrentUser();

        LeaveApplication application = new LeaveApplication();
        application.setApplicationNo(generateApplicationNo("LEAVE"));
        application.setApplicant(applicant);
        application.setApplicationType("LEAVE");
        application.setTitle(request.getTitle() != null ? request.getTitle() : "请假申请");
        application.setLeaveType(LeaveApplication.LeaveType.valueOf(request.getLeaveType()));
        application.setStartDate(request.getStartDate());
        application.setEndDate(request.getEndDate());
        application.setDays(request.getDays());
        application.setReason(request.getReason());
        application.setStatus(Application.ApplicationStatus.DRAFT);

        LeaveApplication saved = leaveApplicationRepository.save(application);

        // Create approval history
        createHistory(saved, ApprovalHistory.HistoryAction.CREATED, "创建申请", null);

        return saved;
    }

    @Transactional
    public ReimbursementApplication createReimbursementApplication(ReimbursementApplicationRequest request) {
        User applicant = getCurrentUser();

        ReimbursementApplication application = new ReimbursementApplication();
        application.setApplicationNo(generateApplicationNo("REIMB"));
        application.setApplicant(applicant);
        application.setApplicationType("REIMBURSEMENT");
        application.setTitle(request.getTitle() != null ? request.getTitle() : "报销申请");
        application.setReimbursementType(ReimbursementApplication.ReimbursementType.valueOf(request.getReimbursementType()));
        application.setAmount(request.getAmount());
        application.setExpenseDate(request.getExpenseDate());
        application.setPurpose(request.getPurpose());
        application.setAttachments(request.getAttachments());
        application.setStatus(Application.ApplicationStatus.DRAFT);

        ReimbursementApplication saved = reimbursementApplicationRepository.save(application);

        // Create approval history
        createHistory(saved, ApprovalHistory.HistoryAction.CREATED, "创建申请", null);

        return saved;
    }

    @Transactional
    public Application submitApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User currentUser = getCurrentUser();
        if (!application.getApplicant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only submit your own applications");
        }

        if (application.getStatus() != Application.ApplicationStatus.DRAFT) {
            throw new RuntimeException("Only draft applications can be submitted");
        }

        application.setStatus(Application.ApplicationStatus.PENDING);
        application.setSubmittedAt(LocalDateTime.now());

        // Create approval workflow
        createApprovalWorkflow(application);

        Application saved = applicationRepository.save(application);

        // Create approval history
        createHistory(saved, ApprovalHistory.HistoryAction.SUBMITTED, "提交申请", null);

        return saved;
    }

    @Transactional
    public Application withdrawApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User currentUser = getCurrentUser();
        if (!application.getApplicant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only withdraw your own applications");
        }

        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            throw new RuntimeException("Only pending applications can be withdrawn");
        }

        application.setStatus(Application.ApplicationStatus.WITHDRAWN);
        Application saved = applicationRepository.save(application);

        // Update pending approval tasks
        List<ApprovalTask> pendingTasks = approvalTaskRepository.findByApplicationOrderByStepOrderAsc(application);
        pendingTasks.stream()
                .filter(task -> task.getStatus() == ApprovalTask.TaskStatus.PENDING)
                .forEach(task -> {
                    task.setStatus(ApprovalTask.TaskStatus.SKIPPED);
                    approvalTaskRepository.save(task);
                });

        // Create approval history
        createHistory(saved, ApprovalHistory.HistoryAction.WITHDRAWN, "撤回申请", null);

        return saved;
    }

    public List<Application> getMyApplications() {
        User currentUser = getCurrentUser();
        return applicationRepository.findByApplicantOrderByCreatedAtDesc(currentUser);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public List<ApprovalHistory> getApplicationHistory(Long applicationId) {
        Application application = getApplicationById(applicationId);
        return approvalHistoryRepository.findByApplicationOrderByCreatedAtAsc(application);
    }

    private void createApprovalWorkflow(Application application) {
        // Simple fixed workflow: Department Manager -> HR Manager
        User applicant = application.getApplicant();

        // Find approvers (for demo, we'll use users with APPROVER role)
        List<User> approvers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.UserRole.APPROVER && u.getEnabled())
                .limit(2)
                .toList();

        if (approvers.isEmpty()) {
            throw new RuntimeException("No approvers found in the system");
        }

        int stepOrder = 1;
        for (User approver : approvers) {
            ApprovalTask task = new ApprovalTask();
            task.setApplication(application);
            task.setApprover(approver);
            task.setStepOrder(stepOrder);
            task.setStepName(stepOrder == 1 ? "部门经理审批" : "HR经理审批");
            task.setStatus(ApprovalTask.TaskStatus.PENDING);
            approvalTaskRepository.save(task);
            stepOrder++;
        }
    }

    private void createHistory(Application application, ApprovalHistory.HistoryAction action, String comment, String details) {
        User currentUser = getCurrentUser();
        ApprovalHistory history = new ApprovalHistory();
        history.setApplication(application);
        history.setUser(currentUser);
        history.setAction(action);
        history.setComment(comment);
        history.setDetails(details);
        approvalHistoryRepository.save(history);
    }

    private String generateApplicationNo(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
