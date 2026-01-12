package com.yir.approvalhub.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class LeaveApplicationRequest {
    
    @NotNull
    private String leaveType;
    
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate endDate;
    
    @NotNull
    private Integer days;
    
    private String reason;
    
    private String title;
}
