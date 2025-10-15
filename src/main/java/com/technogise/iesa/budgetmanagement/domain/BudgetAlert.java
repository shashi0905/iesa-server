package com.technogise.iesa.budgetmanagement.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "budget_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threshold_id", nullable = false)
    private BudgetThreshold threshold;

    @Column(nullable = false)
    private Instant triggeredDate;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private Boolean isAcknowledged = false;

    @Column(name = "acknowledged_date")
    private Instant acknowledgedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (isAcknowledged == null) {
            isAcknowledged = false;
        }
        if (triggeredDate == null) {
            triggeredDate = Instant.now();
        }
    }

    public void acknowledge() {
        if (!this.isAcknowledged) {
            this.isAcknowledged = true;
            this.acknowledgedDate = Instant.now();
        }
    }
}
