package com.technogise.iesa.approvalworkflow.domain;

import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.expensemanagement.domain.ExpenseStatus;
import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Immutable audit trail of expense status transitions.
 * Records who changed the status, when, and why.
 */
@Entity
@Table(name = "workflow_history")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 50)
    private ExpenseStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 50)
    private ExpenseStatus toStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "comment", length = 2000)
    private String comment;

    @Column(name = "timestamp", nullable = false)
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Get the status transition description
     */
    public String getTransitionDescription() {
        if (fromStatus == null) {
            return "Created with status " + toStatus;
        }
        return "Changed from " + fromStatus + " to " + toStatus;
    }

    /**
     * Check if this is an approval transition
     */
    public boolean isApprovalTransition() {
        return toStatus == ExpenseStatus.APPROVED;
    }

    /**
     * Check if this is a rejection transition
     */
    public boolean isRejectionTransition() {
        return toStatus == ExpenseStatus.REJECTED;
    }

    /**
     * Check if this is a submission transition
     */
    public boolean isSubmissionTransition() {
        return fromStatus == ExpenseStatus.DRAFT && toStatus == ExpenseStatus.SUBMITTED;
    }
}
