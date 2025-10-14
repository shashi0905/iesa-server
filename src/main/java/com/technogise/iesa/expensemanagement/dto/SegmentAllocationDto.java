package com.technogise.iesa.expensemanagement.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentAllocationDto {
    private UUID id;
    private UUID segmentId;
    private String segmentName;
    private String segmentCode;
    private BigDecimal amount;
    private BigDecimal percentage;
    private String description;
}
