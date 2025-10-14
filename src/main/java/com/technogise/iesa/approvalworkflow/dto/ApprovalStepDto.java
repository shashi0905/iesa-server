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
public class ApprovalStepDto {
    private UUID id;
    private UUID workflowId;
    private Integer stepOrder;
    private UUID approverRoleId;
    private String approverRoleName;
    private UUID approverUserId;
    private String approverUserName;
    private String condition;
    private Boolean isMandatory;
    private String stepName;
    private Instant createdAt;
    private Instant updatedAt;
}
