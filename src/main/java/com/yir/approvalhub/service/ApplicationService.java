package com.yir.approvalhub.service;

import com.yir.approvalhub.dto.*;
import com.yir.approvalhub.entity.*;
import com.yir.approvalhub.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final ReimbursementApplicationRepository reimbursementApplicationRepository;
    private final UserRepository userRepository;
    private final ApprovalTaskRepository approvalTaskRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                            LeaveApplicationRepository leaveApplicationRepository,
                            ReimbursementApplicationRepository reimbursementApplicationRepository,
                            UserRepository userRepository,
                            ApprovalTaskRepository approvalTaskRepository,
                            ApprovalHistoryRepository approvalHistoryRepository) {
        this.applicationRepository = applicationRepository;
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.reimbursementApplicationRepository = reimbursementApplicationRepository;
        this.userRepository = userRepository;
        this.approvalTaskRepository = approvalTaskRepository;
        this.approvalHistoryRepository = approvalHistoryRepository;
    }

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
        List<Application> applications = applicationRepository.findByApplicantOrderByCreatedAtDesc(currentUser);
        logger.info("用户 {} 查询到 {} 个申请", currentUser.getUsername(), applications.size());
        for (Application app : applications) {
            logger.info("申请 {}: 状态={}, 编号={}", app.getId(), app.getStatus(), app.getApplicationNo());
        }
        return applications;
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
        // Simple fixed workflow: Department Manager only
        User applicant = application.getApplicant();

        // Find approvers by querying database directly for better performance
        List<User> approvers = userRepository.findByRoleAndEnabled(User.UserRole.APPROVER, true).stream()
                .limit(1)
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
            task.setStepName("部门经理审批");
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
        // Use combination of timestamp and random UUID for better uniqueness
        long timestamp = System.currentTimeMillis();
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("%s-%d-%s", prefix, timestamp, randomPart);
    }
}
