package com.technogise.iesa.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for Department entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {

    private UUID id;
    private String name;
    private String code;
    private String description;
    private UUID parentDepartmentId;
    private String parentDepartmentName;
    private UUID managerId;
    private String managerName;
    private String costCenter;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

}
