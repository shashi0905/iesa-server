package com.technogise.iesa.budgetmanagement.domain;

import com.technogise.iesa.usermanagement.domain.Department;
import com.technogise.iesa.segmentmanagement.domain.Segment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id")
    private Segment segment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetPeriod period;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal allocatedAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal consumedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (consumedAmount == null) {
            consumedAmount = BigDecimal.ZERO;
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public BigDecimal getRemainingAmount() {
        return allocatedAmount.subtract(consumedAmount);
    }

    public BigDecimal getUtilizationPercentage() {
        if (allocatedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return consumedAmount.multiply(BigDecimal.valueOf(100))
                .divide(allocatedAmount, 2, BigDecimal.ROUND_HALF_UP);
    }

    public void addConsumption(BigDecimal amount) {
        this.consumedAmount = this.consumedAmount.add(amount);
    }

    public void reduceConsumption(BigDecimal amount) {
        this.consumedAmount = this.consumedAmount.subtract(amount);
        if (this.consumedAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.consumedAmount = BigDecimal.ZERO;
        }
    }
}
