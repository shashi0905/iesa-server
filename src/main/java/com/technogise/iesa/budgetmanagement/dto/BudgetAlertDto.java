package com.technogise.iesa.budgetmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAlertDto {
    private UUID id;
    private UUID budgetId;
    private String budgetName;
    private UUID thresholdId;
    private BigDecimal thresholdPercentage;
    private Instant triggeredDate;
    private String message;
    private Boolean isAcknowledged;
    private Instant acknowledgedDate;
    private Instant createdAt;
}
