package com.technogise.iesa.budgetmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetThresholdDto {
    private UUID id;
    private UUID budgetId;
    private BigDecimal percentage;
    private Boolean alertEnabled;
    private List<UUID> notificationRecipientIds;
    private Instant createdAt;
    private Instant updatedAt;
}
