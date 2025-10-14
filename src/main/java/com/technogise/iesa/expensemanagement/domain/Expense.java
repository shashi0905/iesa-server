package com.technogise.iesa.expensemanagement.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Expense entity representing an expense submission
 */
@Entity
@Table(name = "expenses", indexes = {
    @Index(name = "idx_expense_submitter", columnList = "submitter_id"),
    @Index(name = "idx_expense_status", columnList = "status"),
    @Index(name = "idx_expense_date", columnList = "expense_date"),
    @Index(name = "idx_expense_submission_date", columnList = "submission_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Expense extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submitter_id", nullable = false)
    private User submitter;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "vendor", length = 200)
    private String vendor;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "description", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ExpenseStatus status = ExpenseStatus.DRAFT;

    @Column(name = "submission_date")
    private LocalDate submissionDate;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SegmentAllocation> segmentAllocations = new ArrayList<>();

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    /**
     * Add a segment allocation to this expense
     */
    public void addSegmentAllocation(SegmentAllocation allocation) {
        segmentAllocations.add(allocation);
        allocation.setExpense(this);
    }

    /**
     * Remove a segment allocation from this expense
     */
    public void removeSegmentAllocation(SegmentAllocation allocation) {
        segmentAllocations.remove(allocation);
        allocation.setExpense(null);
    }

    /**
     * Add a document to this expense
     */
    public void addDocument(Document document) {
        documents.add(document);
        document.setExpense(this);
    }

    /**
     * Remove a document from this expense
     */
    public void removeDocument(Document document) {
        documents.remove(document);
        document.setExpense(null);
    }

    /**
     * Check if expense can be edited
     */
    @Transient
    public boolean isEditable() {
        return status == ExpenseStatus.DRAFT || status == ExpenseStatus.REJECTED;
    }

    /**
     * Check if expense can be submitted
     */
    @Transient
    public boolean canBeSubmitted() {
        return status == ExpenseStatus.DRAFT && !segmentAllocations.isEmpty();
    }

    /**
     * Check if expense can be approved
     */
    @Transient
    public boolean canBeApproved() {
        return status == ExpenseStatus.SUBMITTED;
    }

    /**
     * Check if expense can be rejected
     */
    @Transient
    public boolean canBeRejected() {
        return status == ExpenseStatus.SUBMITTED;
    }
}
