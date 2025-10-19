package com.technogise.iesa.budgetmanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateThresholdRequest {

    @DecimalMin(value = "0.0", message = "Percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Percentage must not exceed 100")
    private BigDecimal percentage;

    private Boolean alertEnabled;
}
