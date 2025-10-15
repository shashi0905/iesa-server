package com.technogise.iesa.budgetmanagement.domain;

import com.technogise.iesa.usermanagement.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "budget_thresholds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(nullable = false)
    private Boolean alertEnabled = true;

    @ManyToMany
    @JoinTable(
        name = "budget_threshold_recipients",
        joinColumns = @JoinColumn(name = "threshold_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> notificationRecipients = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (alertEnabled == null) {
            alertEnabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addNotificationRecipient(User user) {
        if (this.notificationRecipients == null) {
            this.notificationRecipients = new HashSet<>();
        }
        this.notificationRecipients.add(user);
    }

    public void removeNotificationRecipient(User user) {
        if (this.notificationRecipients != null) {
            this.notificationRecipients.remove(user);
        }
    }

    public boolean isBreached() {
        if (budget == null) {
            return false;
        }
        BigDecimal utilizationPercentage = budget.getUtilizationPercentage();
        return utilizationPercentage.compareTo(percentage) >= 0;
    }
}
