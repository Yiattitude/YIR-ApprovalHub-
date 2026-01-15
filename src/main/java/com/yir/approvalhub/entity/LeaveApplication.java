package com.yir.approvalhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LeaveApplication extends Application {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer days;

    @Column(length = 1000)
    private String reason;

    public enum LeaveType {
        SICK_LEAVE,
        ANNUAL_LEAVE,
        PERSONAL_LEAVE,
        MATERNITY_LEAVE,
        PATERNITY_LEAVE,
        OTHER
    }
}
