package com.technogise.iesa.budgetmanagement.dto;

import com.technogise.iesa.budgetmanagement.domain.BudgetPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {
    private UUID id;
    private String name;
    private UUID segmentId;
    private String segmentName;
    private UUID departmentId;
    private String departmentName;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal allocatedAmount;
    private BigDecimal consumedAmount;
    private BigDecimal remainingAmount;
    private BigDecimal utilizationPercentage;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
