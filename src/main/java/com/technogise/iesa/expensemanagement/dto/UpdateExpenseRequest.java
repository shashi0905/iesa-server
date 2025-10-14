package com.technogise.iesa.expensemanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpenseRequest {

    private LocalDate expenseDate;

    @Size(max = 200, message = "Vendor name must not exceed 200 characters")
    private String vendor;

    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @Size(min = 3, max = 3, message = "Currency must be 3 characters (ISO code)")
    private String currency;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Valid
    private List<SegmentAllocationRequest> segmentAllocations;
}
