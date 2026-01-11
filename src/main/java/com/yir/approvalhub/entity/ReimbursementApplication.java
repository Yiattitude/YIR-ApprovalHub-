package com.yir.approvalhub.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reimbursement_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReimbursementApplication extends Application {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReimbursementType reimbursementType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 500)
    private String attachments;

    public enum ReimbursementType {
        TRAVEL,
        ENTERTAINMENT,
        OFFICE_SUPPLIES,
        TRAINING,
        COMMUNICATION,
        OTHER
    }
}
