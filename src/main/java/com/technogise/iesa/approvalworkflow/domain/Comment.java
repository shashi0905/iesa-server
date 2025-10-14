package com.technogise.iesa.approvalworkflow.domain;

import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a comment on an expense for collaboration purposes.
 * Can be internal (visible to approvers only) or external (visible to submitter).
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "is_internal", nullable = false)
    @Builder.Default
    private Boolean isInternal = false;

    /**
     * Check if this comment is visible to the submitter
     */
    public boolean isVisibleToSubmitter() {
        return !isInternal;
    }

    /**
     * Mark comment as internal (approvers only)
     */
    public void markAsInternal() {
        this.isInternal = true;
    }

    /**
     * Mark comment as external (visible to submitter)
     */
    public void markAsExternal() {
        this.isInternal = false;
    }
}
