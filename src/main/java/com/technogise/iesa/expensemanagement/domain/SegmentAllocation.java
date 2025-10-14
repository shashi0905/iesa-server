package com.technogise.iesa.expensemanagement.domain;

import com.technogise.iesa.segmentmanagement.domain.Segment;
import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * SegmentAllocation entity representing expense allocation to a segment
 */
@Entity
@Table(name = "segment_allocations", indexes = {
    @Index(name = "idx_segment_alloc_expense", columnList = "expense_id"),
    @Index(name = "idx_segment_alloc_segment", columnList = "segment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SegmentAllocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment segment;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Validate that percentage is between 0 and 100
     */
    @PrePersist
    @PreUpdate
    private void validatePercentage() {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
    }
}
