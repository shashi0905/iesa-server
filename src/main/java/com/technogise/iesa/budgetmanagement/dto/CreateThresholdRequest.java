package com.technogise.iesa.budgetmanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateThresholdRequest {

    @NotNull(message = "Budget ID is required")
    private UUID budgetId;

    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Percentage must not exceed 100")
    private BigDecimal percentage;

    @NotNull(message = "Alert enabled flag is required")
    private Boolean alertEnabled;

    private List<UUID> notificationRecipientIds;
}
