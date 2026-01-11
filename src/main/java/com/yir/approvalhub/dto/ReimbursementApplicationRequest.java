package com.yir.approvalhub.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReimbursementApplicationRequest {
    
    @NotNull
    private String reimbursementType;
    
    @NotNull
    private BigDecimal amount;
    
    @NotNull
    private LocalDate expenseDate;
    
    private String purpose;
    
    private String title;
    
    private String attachments;
}
