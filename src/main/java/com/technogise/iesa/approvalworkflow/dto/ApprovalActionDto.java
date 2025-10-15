package com.technogise.iesa.approvalworkflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalActionDto {
    private UUID id;
    private UUID expenseId;
    private UUID stepId;
    private String stepName;
    private UUID approverId;
    private String approverName;
    private String action;
    private String comment;
    private Instant actionDate;
    private UUID delegatedToId;
    private String delegatedToName;
}
