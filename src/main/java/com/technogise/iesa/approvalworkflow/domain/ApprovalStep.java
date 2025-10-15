package com.technogise.iesa.approvalworkflow.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.Role;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a single step in an approval workflow.
 * Each step defines who can approve (role or specific user) and under what conditions.
 */
@Entity
@Table(name = "approval_steps")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStep extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private ApprovalWorkflow workflow;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_role_id")
    private Role approverRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_user_id")
    private User approverUser;

    @Column(name = "condition", length = 500)
    private String condition;

    @Column(name = "is_mandatory", nullable = false)
    @Builder.Default
    private Boolean isMandatory = true;

    @Column(name = "step_name", length = 200)
    private String stepName;

    /**
     * Check if this step requires a specific user to approve
     */
    public boolean requiresSpecificUser() {
        return approverUser != null;
    }

    /**
     * Check if this step requires a role-based approval
     */
    public boolean requiresRoleApproval() {
        return approverRole != null;
    }
}
