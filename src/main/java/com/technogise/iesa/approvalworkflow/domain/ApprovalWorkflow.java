package com.technogise.iesa.approvalworkflow.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a configurable approval workflow that defines the sequence
 * of approval steps required for expense processing.
 */
@Entity
@Table(name = "approval_workflows")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalWorkflow extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<ApprovalStep> steps = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_conditions", columnDefinition = "jsonb")
    private Map<String, Object> triggerConditions;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Add an approval step to this workflow
     */
    public void addStep(ApprovalStep step) {
        steps.add(step);
        step.setWorkflow(this);
    }

    /**
     * Remove an approval step from this workflow
     */
    public void removeStep(ApprovalStep step) {
        steps.remove(step);
        step.setWorkflow(null);
    }

    /**
     * Activate this workflow
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Deactivate this workflow
     */
    public void deactivate() {
        this.isActive = false;
    }
}
