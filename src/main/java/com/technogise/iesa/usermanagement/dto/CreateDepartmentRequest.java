package com.technogise.iesa.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a new department
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 200, message = "Department name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(max = 50, message = "Department code must not exceed 50 characters")
    private String code;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private UUID parentDepartmentId;

    private UUID managerId;

    @Size(max = 50, message = "Cost center must not exceed 50 characters")
    private String costCenter;

}
