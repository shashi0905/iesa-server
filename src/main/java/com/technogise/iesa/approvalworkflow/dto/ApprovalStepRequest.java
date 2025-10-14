package com.technogise.iesa.approvalworkflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStepRequest {

    @NotNull(message = "Step order is required")
    private Integer stepOrder;

    private UUID approverRoleId;

    private UUID approverUserId;

    @Size(max = 500, message = "Condition must not exceed 500 characters")
    private String condition;

    private Boolean isMandatory;

    @Size(max = 200, message = "Step name must not exceed 200 characters")
    private String stepName;
}
