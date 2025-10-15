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
public class WorkflowHistoryDto {
    private UUID id;
    private UUID expenseId;
    private String fromStatus;
    private String toStatus;
    private UUID actorId;
    private String actorName;
    private String comment;
    private Instant timestamp;
}
