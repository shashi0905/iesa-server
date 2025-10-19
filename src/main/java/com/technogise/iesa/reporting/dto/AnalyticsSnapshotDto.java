package com.technogise.iesa.reporting.dto;

import com.technogise.iesa.reporting.domain.DimensionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSnapshotDto {
    private UUID id;
    private LocalDate snapshotDate;
    private DimensionType dimension;
    private String dimensionValue;
    private BigDecimal totalExpenses;
    private Integer expenseCount;
    private Integer approvedCount;
    private Integer pendingCount;
    private Integer rejectedCount;
    private BigDecimal totalBudgetAllocated;
    private BigDecimal totalBudgetConsumed;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private Instant updatedAt;
}
