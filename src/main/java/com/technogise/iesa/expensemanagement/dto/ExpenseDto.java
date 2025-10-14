package com.technogise.iesa.expensemanagement.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {
    private UUID id;
    private UUID submitterId;
    private String submitterName;
    private LocalDate expenseDate;
    private String vendor;
    private BigDecimal totalAmount;
    private String currency;
    private String description;
    private String status;
    private LocalDate submissionDate;
    private LocalDate approvalDate;
    private String rejectionReason;
    private LocalDate paymentDate;
    private String paymentReference;
    private List<SegmentAllocationDto> segmentAllocations;
    private List<DocumentDto> documents;
    private Instant createdAt;
    private Instant updatedAt;
}
