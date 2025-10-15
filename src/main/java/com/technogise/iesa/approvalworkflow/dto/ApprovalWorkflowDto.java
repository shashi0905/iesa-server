package com.technogise.iesa.approvalworkflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalWorkflowDto {
    private UUID id;
    private String name;
    private String description;
    private List<ApprovalStepDto> steps;
    private Map<String, Object> triggerConditions;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
