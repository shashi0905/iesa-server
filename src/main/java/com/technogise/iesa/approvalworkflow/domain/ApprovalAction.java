package com.technogise.iesa.approvalworkflow.domain;

import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Records an approval action taken on an expense.
 * Tracks who approved/rejected/delegated, when, and any associated comments.
 */
@Entity
@Table(name = "approval_actions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalAction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private ApprovalStep step;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private ApprovalActionType action;

    @Column(name = "comment", length = 2000)
    private String comment;

    @Column(name = "action_date", nullable = false)
    @Builder.Default
    private Instant actionDate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegated_to_id")
    private User delegatedTo;

    /**
     * Check if this action is an approval
     */
    public boolean isApproval() {
        return action == ApprovalActionType.APPROVED;
    }

    /**
     * Check if this action is a rejection
     */
    public boolean isRejection() {
        return action == ApprovalActionType.REJECTED;
    }

    /**
     * Check if this action is a delegation
     */
    public boolean isDelegation() {
        return action == ApprovalActionType.DELEGATED;
    }

    /**
     * Check if this action is a comment
     */
    public boolean isComment() {
        return action == ApprovalActionType.COMMENTED;
    }
}
