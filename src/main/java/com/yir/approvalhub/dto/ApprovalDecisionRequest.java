package com.yir.approvalhub.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApprovalDecisionRequest {
    
    @NotBlank
    private String action; // APPROVE or REJECT
    
    private String comment;
}
