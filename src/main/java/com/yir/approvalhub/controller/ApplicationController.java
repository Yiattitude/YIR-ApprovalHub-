package com.yir.approvalhub.controller;

import com.yir.approvalhub.dto.*;
import com.yir.approvalhub.entity.*;
import com.yir.approvalhub.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/leave")
    public ResponseEntity<?> createLeaveApplication(@Valid @RequestBody LeaveApplicationRequest request) {
        try {
            LeaveApplication application = applicationService.createLeaveApplication(request);
            return ResponseEntity.ok(new ApiResponse(true, "请假申请创建成功", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/reimbursement")
    public ResponseEntity<?> createReimbursementApplication(@Valid @RequestBody ReimbursementApplicationRequest request) {
        try {
            ReimbursementApplication application = applicationService.createReimbursementApplication(request);
            return ResponseEntity.ok(new ApiResponse(true, "报销申请创建成功", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitApplication(@PathVariable Long id) {
        try {
            Application application = applicationService.submitApplication(id);
            return ResponseEntity.ok(new ApiResponse(true, "申请已提交", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawApplication(@PathVariable Long id) {
        try {
            Application application = applicationService.withdrawApplication(id);
            return ResponseEntity.ok(new ApiResponse(true, "申请已撤回", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications() {
        try {
            List<Application> applications = applicationService.getMyApplications();
            return ResponseEntity.ok(new ApiResponse(true, "Success", applications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            Application application = applicationService.getApplicationById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Success", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getApplicationHistory(@PathVariable Long id) {
        try {
            List<ApprovalHistory> history = applicationService.getApplicationHistory(id);
            return ResponseEntity.ok(new ApiResponse(true, "Success", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
