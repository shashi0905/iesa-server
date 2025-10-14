package com.technogise.iesa.expensemanagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentAllocationRequest {

    @NotNull(message = "Segment ID is required")
    private UUID segmentId;

    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0.01", message = "Percentage must be greater than 0")
    @DecimalMax(value = "100.00", message = "Percentage must not exceed 100")
    private BigDecimal percentage;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
