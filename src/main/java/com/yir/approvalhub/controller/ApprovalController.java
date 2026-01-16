package com.yir.approvalhub.controller;

import com.yir.approvalhub.dto.ApiResponse;
import com.yir.approvalhub.dto.ApprovalDecisionRequest;
import com.yir.approvalhub.entity.ApprovalTask;
import com.yir.approvalhub.service.ApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getMyPendingTasks() {
        try {
            List<ApprovalTask> tasks = approvalService.getMyPendingTasks();
            return ResponseEntity.ok(new ApiResponse(true, "Success", tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<?> getApplicationTasks(@PathVariable Long applicationId) {
        try {
            List<ApprovalTask> tasks = approvalService.getApplicationTasks(applicationId);
            return ResponseEntity.ok(new ApiResponse(true, "Success", tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{taskId}/process")
    public ResponseEntity<?> processApprovalTask(@PathVariable Long taskId, 
                                                  @Valid @RequestBody ApprovalDecisionRequest request) {
        try {
            ApprovalTask task = approvalService.processApprovalTask(taskId, request);
            return ResponseEntity.ok(new ApiResponse(true, "审批处理成功", task));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
