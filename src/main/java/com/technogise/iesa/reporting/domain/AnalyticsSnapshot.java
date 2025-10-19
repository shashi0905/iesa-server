package com.technogise.iesa.reporting.domain;

import com.technogise.iesa.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Entity representing pre-aggregated analytics data for performance
 */
@Entity
@Table(name = "analytics_snapshots", indexes = {
    @Index(name = "idx_snapshot_date_dimension", columnList = "snapshot_date, dimension"),
    @Index(name = "idx_snapshot_dimension_value", columnList = "dimension, dimension_value")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_snapshot_unique",
        columnNames = {"snapshot_date", "dimension", "dimension_value"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AnalyticsSnapshot extends BaseEntity {

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "dimension", nullable = false, length = 50)
    private DimensionType dimension;

    @Column(name = "dimension_value", nullable = false, length = 200)
    private String dimensionValue;

    @Column(name = "total_expenses", precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalExpenses = BigDecimal.ZERO;

    @Column(name = "expense_count", nullable = false)
    @Builder.Default
    private Integer expenseCount = 0;

    @Column(name = "approved_count", nullable = false)
    @Builder.Default
    private Integer approvedCount = 0;

    @Column(name = "pending_count", nullable = false)
    @Builder.Default
    private Integer pendingCount = 0;

    @Column(name = "rejected_count", nullable = false)
    @Builder.Default
    private Integer rejectedCount = 0;

    @Column(name = "total_budget_allocated", precision = 19, scale = 2)
    private BigDecimal totalBudgetAllocated;

    @Column(name = "total_budget_consumed", precision = 19, scale = 2)
    private BigDecimal totalBudgetConsumed;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
